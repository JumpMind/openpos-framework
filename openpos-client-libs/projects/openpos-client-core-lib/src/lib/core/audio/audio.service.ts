import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, merge, Observable, Subject } from 'rxjs';
import { filter, map, skip, switchMap, take, takeUntil } from 'rxjs/operators';
import { SessionService } from '../services/session.service';
import { MessageTypes } from '../messages/message-types';
import { AudioGroup, AudioRequest } from './audio-request.interface';
import { AudioMessage } from './audio-message.interface';
import { AudioUtil } from './audio-util';
import { AudioRepositoryService } from './audio-repository.service';
import { AudioConfig } from './audio-config.interface';
import { DialogService } from '../services/dialog.service';
import { AudioPlayRequest } from './audio-play-request.interface';
import { PersonalizationTokenService } from '../personalization/personalization-token.service';

@Injectable({
    providedIn: 'root',
})
export class AudioService implements OnDestroy {
    private static readonly NO_GROUP = 'NO_GROUP';

    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private config = AudioUtil.getDefaultConfig();

    audioGroups: { [group: string]: AudioGroup } = {};

    beforePlay$ = new Subject<AudioPlayRequest>();
    playing$ = new BehaviorSubject<AudioPlayRequest>({audio: null, request: null});
    audioMessage$ = new Subject<AudioMessage>();

    waitForDialogQueue: AudioPlayRequest[] = [];

    constructor(private sessionService: SessionService,
                private dialogService: DialogService,
                private personalizationTokenService: PersonalizationTokenService,
                private audioRepositoryService: AudioRepositoryService) {
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listen(): void {
        this.config = this.audioRepositoryService.config$.getValue();

        if (!this.config.enabled) {
            console.log(`[AudioService]: Not listening for ${MessageTypes.AUDIO} messages because audio is disabled`);
            return;
        }

        console.log(`[AudioService]: Listening for ${MessageTypes.AUDIO} messages...`);

        // Play sounds queued to play after dialog is opened
        this.dialogService.afterOpened$
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(() => this.onAfterDialogOpened());

        // Listen for configuration changes
        this.audioRepositoryService.config$
            .pipe(
                skip(1),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(config => this.onAudioConfig(config));

        // Listen for audio messages
        this.sessionService.getMessages()
            .pipe(
                filter(message => message.type === MessageTypes.AUDIO),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(message => this.onAudioMessage(message));
    }

    stopListening(): void {
        console.log('[AudioService]: Stopped taking requests');
        this.stop$.next();
    }

    play(request: AudioRequest): Observable<AudioPlayRequest> {
        if (!this.config.enabled) {
            console.warn('[AudioService]: Ignoring audio request because the service is disabled', request);
            return;
        }

        const actualRequest = AudioUtil.getDefaultRequest(request);

        if (actualRequest.sound && !actualRequest.url) {
            // If only a sound, and no URL is provided, we need to publish the request to the server so the
            // content URL can be resolved
            return this.publishPlaySoundRequest(actualRequest);
        }

        if (!actualRequest.url) {
            console.warn('[AudioService]: Ignoring null request', actualRequest);
            return;
        }

        console.log('[AudioService]: Getting audio from repository', actualRequest);
        const audio$ = this.audioRepositoryService.getAudio(actualRequest);

        audio$.pipe(
            take(1)
        ).subscribe(audio => this.onAudioReady({audio, request: actualRequest}));

        return audio$.pipe(map(audio => ({audio, request: actualRequest})));
    }

    publishPlaySoundRequest(request: AudioRequest): Observable<AudioPlayRequest> {
        const actualRequest = AudioUtil.getDefaultRequest(request);
        // Disable autoplay so this method can handle playing the sound
        actualRequest.autoplay = false;
        const actualRequestHash = AudioUtil.getAudioRequestHash(actualRequest);

        const audioMessage$ = this.audioMessage$.pipe(
            filter(message => {
                // Update "autoplay" and "url" to match the original request when checking the hashes
                const messageRequest = {...message.request};
                messageRequest.autoplay = false;
                messageRequest.url = null;
                return AudioUtil.getAudioRequestHash(messageRequest) === actualRequestHash;
            }),
            take(1),
            switchMap(message => {
                // Switch back autoplay to "true"
                message.request.autoplay = true;
                return this.play(message.request);
            })
        );
        audioMessage$.subscribe();

        this.sessionService.publish('Play', 'Audio', actualRequest);

        return Observable.create((subscriber: Subject<AudioPlayRequest>) => {
            audioMessage$.subscribe(playRequest => {
                subscriber.next(playRequest);
                subscriber.complete();
            });
        });
    }

    playRequest(playRequest: AudioPlayRequest): void {
        const audio = playRequest.audio;
        const request = playRequest.request;

        if (request.endTime) {
            let timeupdateCount = 0;

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

        if (request.delayTime) {
            const delaySeconds = request.delayTime * 1000;
            console.log(`[AudioService]: Delayed playing by ${delaySeconds} seconds`, request);
            setTimeout(() => this.makeSound(playRequest), delaySeconds);
        } else if (request.waitForScreen) {
            console.log('[AudioService]: Waiting for screen before playing...', playRequest.request);
            setTimeout(() => this.makeSound(playRequest), 0);
        } else {
            console.log('[AudioService]: Playing', request);
            this.makeSound(playRequest);
        }
    }

    playQueue(playRequests: AudioPlayRequest[]): void {
        playRequests.forEach(playRequest => {
            setTimeout(() => this.playRequest(playRequest), 0);
        });
    }

    makeSound(playRequest: AudioPlayRequest): void {
        const audio = playRequest.audio;
        const request = {...playRequest.request};

        request.url = this.personalizationTokenService.replaceTokens(request.url);

        if (request.group) {
            this.addToAudioGroup(audio, request);
            this.stop(request.group, audio);
        }

        this.updateAudioWithRequest(audio, request);

        if (request.autoplay !== false) {
            this.beforePlay$.next(playRequest);
            audio.play();
            this.playing$.next(playRequest);
        }
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
                    .filter(audio => audio.readyState > HTMLMediaElement.HAVE_CURRENT_DATA)
                    .forEach(audio => audio.pause());
            });
    }

    updateAudioWithRequest(audio: HTMLMediaElement, request: AudioRequest): void {
        // Adjust the volume of this sound by the global configuration volume
        audio.volume = (request.volume || 1) * (this.config.volume || 1);

        // Only honor autoplay setting if there's no setting that delays the playing
        audio.autoplay = request.autoplay
            && !request.delayTime
            && !request.waitForScreen
            && !request.waitForDialog;

        audio.currentTime = request.startTime || 0;
        audio.playbackRate = request.playbackRate || 1;
        audio.loop = request.loop;
    }

    addToAudioGroup(audio: HTMLAudioElement, request: AudioRequest): void {
        if (!request.group) {
            return;
        }

        const group = request.group || AudioService.NO_GROUP;
        this.audioGroups[group] = this.audioGroups[group] || {};

        const key = AudioUtil.getAudioRequestHash(request);
        this.audioGroups[group][key] = audio;
    }

    onAudioReady(playRequest: AudioPlayRequest): void {
        if (playRequest.request.waitForDialog) {
            console.log('[AudioService]: Waiting for dialog before playing...', playRequest.request);
            this.waitForDialogQueue.push(playRequest);
        } else {
            this.playRequest(playRequest);
        }
    }

    onAudioMessage(message: AudioMessage): void {
        console.log('[AudioService]: Received message', message);

        this.audioMessage$.next(message);
        const request = {...message.request};

        if (this.dialogService.isDialogOpening()) {
            request.waitForDialog = true;
        } else if (!this.dialogService.isDialogOpen()) {
            request.waitForScreen = true;
        }

        this.audioMessage$.next(message);
        this.play(request);
    }

    onAudioConfig(config: AudioConfig): void {
        console.log('[AudioService]: Configuration updated', config);
        this.config = AudioUtil.getDefaultConfig(config);

        this.stopListening();
        this.listen();
    }

    onAfterDialogOpened(): void {
        if (this.waitForDialogQueue.length > 0) {
            console.log(`[AudioService]: Playing ${this.waitForDialogQueue.length} audio file(s) after dialog opened`);
        }

        this.playQueue(this.waitForDialogQueue);
        this.waitForDialogQueue = [];
    }
}