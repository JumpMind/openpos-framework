import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, merge, Observable, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';
import { SessionService } from '../services/session.service';
import { MessageTypes } from '../messages/message-types';
import { AudioGroup, AudioRequest } from './audio-request.interface';
import { AudioMessage } from './audio-message.interface';
import { AudioUtil } from './audio-util';
import { AudioRepositoryService } from './audio-repository.service';
import { AudioConfig } from './audio-config.interface';
import { IMessageHandler } from '../interfaces/message-handler.interface';
import { LifeCycleEvents } from '../messages/life-cycle-events.enum';
import { DialogService } from '../services/dialog.service';
import { AudioPlayRequest } from './audio-play-request.interface';

@Injectable({
    providedIn: 'root',
})
export class AudioService implements OnDestroy, IMessageHandler<any> {
    private static readonly NO_GROUP = 'NO_GROUP';

    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private config = AudioUtil.getDefaultConfig();

    audioGroups: { [group: string]: AudioGroup } = {};

    beforePlay$ = new Subject<AudioPlayRequest>();
    playing$ = new BehaviorSubject<AudioPlayRequest>({audio: null, request: null});

    waitForDialogQueue: AudioPlayRequest[] = [];
    waitForScreenQueue: AudioPlayRequest[] = [];

    constructor(private sessionService: SessionService,
                private dialogService: DialogService,
                private audioRepositoryService: AudioRepositoryService) {

        this.sessionService.registerMessageHandler(this);
        this.dialogService.afterOpened$.pipe(takeUntil(this.destroyed$)).subscribe(() => this.onAfterDialogOpened());
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listen(): void {
        console.log(`[AudioService]: Listening for ${MessageTypes.AUDIO} messages...`);

        this.sessionService.getMessages(MessageTypes.AUDIO)
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(message => this.onAudioMessage(message));

        this.audioRepositoryService.config$
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(config => this.onAudioConfig(config))
    }

    stopListening(): void {
        console.log('[AudioService]: Stopped taking requests');
        this.stop$.next();
    }

    play(request: string | AudioRequest): Observable<HTMLAudioElement> {
        if (!this.config.enabled) {
            console.warn('[AudioService]: Ignoring audio request because the service is disabled', request);
            return;
        }

        if (!request || !(request as AudioRequest).sound) {
            console.warn('[AudioService]: Ignoring null request');
            return;
        }

        let actualRequest = (typeof request === 'string' ? {sound: request} as AudioRequest : request) as AudioRequest;
        actualRequest = AudioUtil.getDefaultRequest(actualRequest);

        console.log('[AudioService]: Getting audio from repository', actualRequest);
        const audio$ = this.audioRepositoryService.getAudio(actualRequest);

        audio$.pipe(
            take(1)
        ).subscribe(audio => {
            this.addToAudioGroup(actualRequest, audio);

            if (actualRequest.waitForDialog) {
                console.log('[AudioService]: Waiting for dialog before playing...', actualRequest);
                this.waitForDialogQueue.push({audio, request: actualRequest});
            } else if (actualRequest.waitForScreen) {
                console.log('[AudioService]: Waiting for screen before playing...');
                this.waitForScreenQueue.push({audio, request: actualRequest});
            } else {
                this.playRequest(audio, actualRequest);
            }
        });

        return audio$;
    }

    playRequest(audio: HTMLAudioElement, request: AudioRequest): void {
        this.beforePlay$.next({audio, request: request});

        let timeupdateCount = 0;

        if (request.endTime) {
            // Fake the end time by pausing the audio once the end time is reached
            audio.addEventListener('timeupdate', () => {
                // For some reason this callback would sometimes get called after removing it.
                // So a more reliable way to stop responding is with a counter.
                if (audio.currentTime >= request.endTime && !audio.paused && timeupdateCount === 0) {
                    timeupdateCount++;
                    console.log(`[AudioService]: Reached end time ${request.endTime}`, request);
                    audio.pause();
                }
            });
        }

        const makeSound = () => {
            if (request.group) {
                this.addToAudioGroup(request, audio);
                this.stop(request.group, audio);
            }

            audio.volume = (request.volume || 1) * (this.config.volume || 1);

            if (request.autoplay !== false) {
                audio.play();
            }
        };

        if (request.delayTime) {
            const delaySeconds = request.delayTime * 1000;
            console.log(`[AudioService]: Delayed playing by ${delaySeconds} seconds`, request);
            window.setTimeout(() => makeSound(), delaySeconds);
        } else {
            console.log('[AudioService]: Playing', request);
            makeSound();
        }

        this.playing$.next({audio, request: request});
    }

    addToAudioGroup(request: AudioRequest, audio: HTMLAudioElement): void {
        if (!request.group) {
            return;
        }

        const group = request.group || AudioService.NO_GROUP;
        this.audioGroups[group] = this.audioGroups[group] || {};

        const key = AudioUtil.getAudioRequestHash(request);
        this.audioGroups[group][key] = audio;
    }

    stop(group?: string, skipThisAudio?: HTMLMediaElement): void {
        Object.keys(this.audioGroups)
            // If there's no group then stop all groups
            .filter(key => !group || key === group)
            .map(group => this.audioGroups[group])
            .forEach(audioGroup => {
                const audioListInGroup = Object.keys(audioGroup).map(key => audioGroup[key]);
                // Stop all of the audio in this group
                audioListInGroup
                    .filter(audio => audio !== skipThisAudio)
                    .forEach(audio => audio.pause());
            });
    }

    onAudioMessage(message: AudioMessage): void {
        console.log('[AudioService]: Received message', message);

        const request = {...message.request};

        if (this.dialogService.isDialogOpening()) {
            request.waitForDialog = true;
        } else if (!this.dialogService.isDialogOpen()) {
            request.waitForScreen;
        }

        this.play(request);
    }

    onAudioConfig(config: AudioConfig): void {
        console.log('[AudioService]: Configuration updated', config);

        if (this.config.enabled && !config.enabled) {
            this.stopListening();
        }

        if (!this.config.enabled && config.enabled) {
            this.listen();
        }

        this.config = config;
    }

    handle(message: any) {
        if (message.eventType === LifeCycleEvents.ScreenUpdated) {
            if (this.waitForScreenQueue.length > 0) {
                console.log(`[AudioService]: Playing ${this.waitForScreenQueue.length} audio file(s) after screen rendered`);
            }

            this.playQueue(this.waitForScreenQueue);
            this.waitForScreenQueue = [];
        }
    }

    onAfterDialogOpened(): void {
        if (this.waitForDialogQueue.length > 0) {
            console.log(`[AudioService]: Playing ${this.waitForDialogQueue.length} audio file(s) after dialog opened`);
        }

        this.playQueue(this.waitForDialogQueue);
        this.waitForDialogQueue = [];
    }

    playQueue(playRequests: AudioPlayRequest[]): void {
        playRequests.forEach(playRequest => {
            setTimeout(() => this.playRequest(playRequest.audio, playRequest.request), 0);
        });
    }
}