import { Injectable, OnDestroy } from '@angular/core';
import { fromEvent, merge, Observable, Subject } from 'rxjs';
import { filter, skip, takeUntil, tap } from 'rxjs/operators';
import { AudioRequest } from './audio-request.interface';
import { AudioService } from './audio.service';
import { AudioConfig } from './audio-config.interface';
import { AudioRepositoryService } from './audio-repository.service';
import { DialogService } from '../services/dialog.service';
import { AudioUtil } from './audio-util';
import { AudioPlayRequest } from './audio-play-request.interface';

@Injectable({
    providedIn: 'root',
})
export class AudioInteractionService implements OnDestroy {
    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private config = AudioUtil.getDefaultConfig();

    constructor(private audioRepositoryService: AudioRepositoryService,
                private dialogService: DialogService,
                private audioService: AudioService) {
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listen(): void {
        this.config = this.audioRepositoryService.config$.getValue();

        if (!this.isEnabled()) {
            console.log('[AudioInteractionService]: Not listening to the user because audio interaction is disabled');
            return;
        }

        console.log('[AudioInteractionService]: Listening to the user...');

        this.listenForMouseInteractions();
        this.listenForTouchInteractions();
        this.listenForDialogInteractions();

        this.audioRepositoryService.config$
            .pipe(
                skip(1),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(config => this.onAudioConfig(config));
    }

    stopListening(): void {
        console.log('[AudioInteractionService]: Stopped listening to the user');
        this.stop$.next();
    }

    isEnabled(interactionKey?: string): boolean {
        let enabled = this.config.enabled && this.config.interactions.enabled;

        if (interactionKey && this.config.interactions[interactionKey]) {
            enabled = this.config.interactions[interactionKey].enabled !== false;
        }

        return enabled;
    }

    listenForMouseInteractions(): void {
        // Use "capture" so this service hears the event even if propagation is stopped
        const mouseDown$ = fromEvent<MouseEvent>(document.body, 'mousedown', {capture: true});
        const mouseUp$ = fromEvent<MouseEvent>(document.body, 'mouseup', {capture: true});
        const mouseEvents$ = merge(mouseDown$, mouseUp$);

        mouseEvents$.pipe(
            filter(() => this.isEnabled('mouse')),
            takeUntil(merge(this.stop$, this.destroyed$))
        ).subscribe(event => {
            if (event.type === 'mousedown') {
                console.log('[AudioInteractionService]: Playing button mouse-down sound');
                this.play(this.config.interactions.mouse.mouseDown);
            } else if (event.type === 'mouseup') {
                console.log('[AudioInteractionService]: Playing button mouse-up sound')
                this.play(this.config.interactions.mouse.mouseUp);
            }
        });
    }

    listenForTouchInteractions(): void {
        // Use "capture" so this service hears the event even if propagation is stopped
        const touchStart$ = fromEvent<MouseEvent>(document.body, 'touchstart', {capture: true});
        const touchEnd$ = fromEvent<MouseEvent>(document.body, 'touchend', {capture: true});
        const touchEvents$ = merge(touchStart$, touchEnd$);

        touchEvents$.pipe(
            filter(() => this.isEnabled('touch')),
            takeUntil(merge(this.stop$, this.destroyed$))
        ).subscribe(event => {
            if (event.type === 'touchstart') {
                console.log('[AudioInteractionService]: Playing button touch-start sound');
                this.play(this.config.interactions.touch.touchStart);
            } else if (event.type === 'touchend') {
                console.log('[AudioInteractionService]: Playing button touch-end sound')
                this.play(this.config.interactions.touch.touchEnd);
            }
        });
    }

    listenForDialogInteractions(): void {
        this.dialogService.beforeOpened$
            .pipe(
                filter(() => this.isEnabled('dialog')),
                tap(() => console.log('[AudioInteractionService]: Playing dialog opening sound')),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(() => this.play(this.config.interactions.dialog.opening));

        this.dialogService.beforeClosed$
            .pipe(
                filter(() => this.isEnabled('dialog')),
                tap(() => console.log('[AudioInteractionService]: Playing dialog closing sound')),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(() => this.play(this.config.interactions.dialog.closing));
    }

    onAudioConfig(config: AudioConfig): void {
        console.log('[AudioInteractionService]: Configuration updated', config);
        this.config = AudioUtil.getDefaultConfig(config);

        this.stopListening();
        this.listen();
    }

    play(audioRequest: AudioRequest): Observable<AudioPlayRequest> {
        return this.audioService.play(audioRequest);
    }
}