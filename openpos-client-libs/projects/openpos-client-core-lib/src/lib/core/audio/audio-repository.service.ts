import { Injectable, OnDestroy } from '@angular/core';
import { SessionService } from '../services/session.service';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { AudioConfig } from './audio-config.interface';
import { MessageTypes } from '../messages/message-types';
import { filter, first, switchMap, takeUntil, tap } from 'rxjs/operators';
import { AudioConfigMessage } from './audio-config-message.interface';
import { AudioCache } from './audio-cache.interface';
import { AudioRequest } from './audio-request.interface';
import { PersonalizationService } from '../personalization/personalization.service';
import { deepAssign } from '../../utilites/deep-assign';
import { AudioUtil } from './audio-util';
import { AudioPreloadMessage } from './audio-preload-message.interface';
import { PersonalizationTokenService } from '../personalization/personalization-token.service';

@Injectable({
    providedIn: 'root'
})
export class AudioRepositoryService implements OnDestroy {
    private destroyed$ = new Subject();
    private cache: AudioCache = {};

    preloading$ = new BehaviorSubject(false);
    config: AudioConfig = AudioUtil.getDefaultConfig();
    config$ = new BehaviorSubject<AudioConfig>(AudioUtil.getDefaultConfig());

    constructor(private sessionService: SessionService,
                private personalizationService: PersonalizationService,
                private personalizationTokenService: PersonalizationTokenService) {
        this.listenForMessages();
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    loadConfig(): void {
        console.log('[AudioRepositoryService]: Loading audio configuration...');
        this.sessionService.publish('GetConfig', 'Audio');
    }

    preloadAudio(): void {
        this.preloading$.next(true);
        console.log('[AudioRepositoryService]: Preloading audio...');
        this.sessionService.publish('Preload', 'Audio');
    }

    getAudio(request: string | AudioRequest): Observable<HTMLAudioElement> {
        request = typeof request === 'string' ? {sound: request} : request;

        if (this.preloading$.getValue()) {
            console.log('[AudioRepositoryService]: Skipping request while audio is preloading...', request);
            return;
        }

        request = AudioUtil.getDefaultRequest(request);

        const cacheKey = AudioUtil.getAudioRequestHash(request);
        const audio = this.cache[cacheKey];

        if (audio) {
            console.log('[AudioRepositoryService]: Loaded audio from the cache', audio);

            // Pause this instance in case it was in the middle of playing
            audio.pause();

            // Make sure the start time is correct, because if this instance was previously played
            // from the cache, then the start time will not be correct.
            audio.currentTime = request.startTime;

            return of(audio);
        }

        console.log('[AudioRepositoryService]: Creating audio for request', request);

        return this.createAudio(request)
            .pipe(
                tap(audio => this.cache[cacheKey] = audio)
            );
    }

    createAudio(request: AudioRequest): Observable<HTMLAudioElement> {
        request = AudioUtil.getDefaultRequest(request);

        const url = this.personalizationTokenService.replaceTokens(request.sound);
        const audio = new Audio(url);

        // Adjust the volume of this sound by the global configuration volume
        audio.volume = request.volume * this.config.volume;

        // Only honor autoplay setting if there's no setting that delays the playing
        audio.autoplay = request.autoplay
            && !request.delayTime
            && !request.waitForScreen
            && !request.waitForDialog;

        audio.currentTime = request.startTime;
        audio.playbackRate = request.playbackRate;
        audio.loop = request.loop;

        const audio$ = new Subject<HTMLAudioElement>();
        audio.addEventListener('canplay', () => audio$.next(audio));

        return audio$;
    }

    listenForMessages(): void {
        this.sessionService.getMessages(MessageTypes.AUDIO_PRELOAD)
            .pipe(
                takeUntil(this.destroyed$)
            ).subscribe(message => this.onAudioPreloadMessage(message));

        this.sessionService.getMessages(MessageTypes.AUDIO_CONFIG)
            .pipe(
                takeUntil(this.destroyed$)
            ).subscribe(message => this.onAudioConfigMessage(message));
    }

    fillBrowserCache(contentUrls: string[]): void {
        let loadedCount = 0;

        contentUrls
            .map(contentUrl => this.personalizationTokenService.replaceTokens(contentUrl))
            // Loading each audio file will cause the browser to cache subsequent requests
            .map(url => new Audio(url))
            .forEach(audio => {
                audio.oncanplay = () => {
                    loadedCount++;

                    if (loadedCount === contentUrls.length) {
                        console.log(`[AudioRepositoryService]: Finished preloading ${contentUrls.length} audio files`, contentUrls);
                        this.preloading$.next(false);
                    }
                };
            });
    }

    onAudioConfigMessage(message: AudioConfigMessage): void {
        console.log('[AudioRepositoryService]: Received config message', message);

        deepAssign(this.config, message.config);
        this.config$.next(this.config);
    }

    onAudioPreloadMessage(message: AudioPreloadMessage): void {
        this.preloading$.next(false);
        console.log('[AudioRepositoryService]: Received preload message', message);
        this.fillBrowserCache(message.contentUrls);
    }
}