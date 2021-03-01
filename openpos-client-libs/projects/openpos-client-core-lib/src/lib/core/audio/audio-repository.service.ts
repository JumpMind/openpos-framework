import { Injectable, OnDestroy } from '@angular/core';
import { SessionService } from '../services/session.service';
import { BehaviorSubject, EMPTY, Observable, of, Subject } from 'rxjs';
import { AudioConfig } from './audio-config.interface';
import { MessageTypes } from '../messages/message-types';
import { skip, take, takeUntil, tap } from 'rxjs/operators';
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

    loadConfig(): Observable<AudioConfig> {
        console.log('[AudioRepositoryService]: Loading audio configuration...');
        this.sessionService.publish('GetConfig', 'Audio');

        return this.config$.pipe(
            skip(1),
            take(1)
        );
    }

    preloadAudio(): Observable<AudioCache> {
        this.preloading$.next(true);
        console.log('[AudioRepositoryService]: Preloading audio...');
        this.sessionService.publish('Preload', 'Audio');

        return Observable.create(subscriber => {
            this.preloading$.pipe(skip(1)).subscribe(config => {
                subscriber.next(this.cache);
                subscriber.complete();
            });
        });
    }

    getAudio(request: string | AudioRequest): Observable<HTMLAudioElement> {
        if (!request) {
            return of(null);
        }

        let actualRequest = typeof request === 'string' ? {url: request} : request;
        actualRequest = AudioUtil.getDefaultRequest(actualRequest);

        if (this.preloading$.getValue()) {
            console.log('[AudioRepositoryService]: Skipping request while audio is preloading...', actualRequest);
            return EMPTY;
        }

        const cacheKey = AudioUtil.getAudioRequestHash(actualRequest);
        const audio = this.cache[cacheKey];

        if (audio) {
            console.log('[AudioRepositoryService]: Loaded audio from the cache', audio);

            // Pause this instance in case it was in the middle of playing
            if (AudioUtil.isPlaying(audio)) {
                audio.pause();
            }

            // Make sure the start time is correct, because if this instance was previously played
            // from the cache, then the start time will not be correct.
            audio.currentTime = actualRequest.startTime;

            return of(audio);
        }

        console.log('[AudioRepositoryService]: Creating audio for request', actualRequest);

        return this.createAudio(actualRequest)
            .pipe(
                tap(audio => this.cache[cacheKey] = audio)
            );
    }

    createAudio(request: AudioRequest): Observable<HTMLAudioElement> {
        request = AudioUtil.getDefaultRequest(request);

        const url = this.personalizationTokenService.replaceTokens(request.url);
        const audio = new Audio(url);
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

    fillBrowserCache(urls: string[]): Observable<any> {
        const done$ = new Subject<string[]>();
        let loadedCount = 0;

        if (urls.length === 0) {
            return of([]);
        }

        urls
            .map(url => this.personalizationTokenService.replaceTokens(url))
            // Loading each audio file will cause the browser to cache subsequent requests
            .forEach(url => {
                const audio = new Audio();

                audio.oncanplay = () => {
                    loadedCount++;

                    if (loadedCount === urls.length) {
                        console.log(`[AudioRepositoryService]: Finished preloading ${urls.length} audio files`, urls);
                        done$.next();
                        done$.complete();
                    }
                };
                audio.onerror = error => {
                    console.error(`[AudioRepositoryService]: Failed to preload ${url}`);
                    done$.next();
                    done$.error(error);
                };
                audio.src = url;
                audio.load();
            });

        return done$;
    }

    onAudioConfigMessage(message: AudioConfigMessage): void {
        console.log('[AudioRepositoryService]: Received config message', message);

        deepAssign(this.config, message.config);
        this.config$.next(this.config);
    }

    onAudioPreloadMessage(message: AudioPreloadMessage): void {
        console.log('[AudioRepositoryService]: Received preload message', message);
        this.fillBrowserCache(message.urls).subscribe(() => this.preloading$.next(false));
    }
}