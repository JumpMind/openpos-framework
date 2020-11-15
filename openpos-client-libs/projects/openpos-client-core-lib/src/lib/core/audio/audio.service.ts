import { Injectable, OnDestroy } from '@angular/core';
import { merge, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AudioEventArg } from './audio-event-arg.interface';
import { SessionService } from '../services/session.service';
import { MessageTypes } from '../messages/message-types';
import { AudioRequest } from './audio.request.interface';
import { AudioMessage } from './audio-message.interface';
import { AudioUtil } from './audio.util';
import { AudioRepositoryService } from './audio-repository.service';
import { AudioConfig } from './audio-config.interface';

@Injectable({
    providedIn: 'root',
})
export class AudioService implements OnDestroy {
    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private config = AudioUtil.getDefaultConfig();

    beforePlay$ = new Subject<AudioEventArg>();
    playing$ = new Subject<AudioEventArg>();

    constructor(private sessionService: SessionService, private audioRepositoryService: AudioRepositoryService) {
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
        if (!request || !(request as AudioRequest).sound) {
            console.warn('[AudioService]: Ignoring null request');
            return;
        }

        if (!this.config.enabled) {
            console.warn('[AudioService]: Ignoring audio request because the service is disabled', request);
            return;
        }

        let audioRequest = typeof request === 'string' ? {sound: request} as AudioRequest : request;
        audioRequest = AudioUtil.getDefaultRequest(audioRequest);

        const audio$ = this.audioRepositoryService.getAudio(request);

        audio$.subscribe(audio => {
            this.beforePlay$.next({audio, request: audioRequest});

            if (audioRequest.autoplay === true) {
                if (audioRequest.delayTime) {
                    const delaySeconds = audioRequest.delayTime * 1000;
                    console.log(`[AudioService]: Delayed playing by ${delaySeconds} seconds`, audio, audioRequest);
                    window.setTimeout(() => audio.play(), delaySeconds);
                } else {
                    console.log('[AudioService]: Playing', audio, audioRequest);
                    audio.play();
                }

                this.playing$.next({audio, request: audioRequest});
            }
        });

        return audio$;
    }

    onAudioMessage(message: AudioMessage): void {
        console.log('[AudioService]: Received message', message);
        this.play(message.request);
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
}