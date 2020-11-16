import { Injectable, OnDestroy } from '@angular/core';
import { fromEvent, merge, Observable, Subject } from 'rxjs';
import { filter, takeUntil, tap } from 'rxjs/operators';
import { AudioInteractionSet } from './audio-interaction-set.interface';
import { AudioRequest } from './audio-request.interface';
import { AudioService } from './audio.service';
import { AudioConfig } from './audio-config.interface';
import { AudioRepositoryService } from './audio-repository.service';
import { DialogService } from '../services/dialog.service';

@Injectable({
    providedIn: 'root',
})
export class AudioInteractionService implements OnDestroy {
    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private enabled = true;
    private interactions: AudioInteractionSet;

    constructor(private audioRepositoryService: AudioRepositoryService,
                private dialogService: DialogService,
                private audioService: AudioService) {
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listen(): void {
        console.log('[AudioInteractionService]: Listening to the user...');

        this.listenForMouseInteractions();
        this.listenForTouchInteractions();
        this.listenForDialogInteractions();

        this.audioRepositoryService.config$
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(config => this.onAudioConfig(config));
    }

    stopListening(): void {
        console.log('[AudioInteractionService]: Stopped listening to the user');
        this.stop$.next();
    }

    listenForMouseInteractions(): void {
        // Use "capture" so this service hears the event even if propagation is stopped
        const mouseDown$ = fromEvent<MouseEvent>(document.body, 'mousedown', {capture: true});
        const mouseUp$ = fromEvent<MouseEvent>(document.body, 'mouseup', {capture: true});
        const mouseEvents$ = merge(mouseDown$, mouseUp$);

        mouseEvents$.pipe(
            filter(() => !!this.interactions && !!this.interactions.mouse),
            takeUntil(merge(this.stop$, this.destroyed$))
        ).subscribe(event => {
            if (event.type === 'mousedown') {
                console.log('[AudioInteractionService]: Playing button mouse-down sound');
                this.play(this.interactions.mouse.mouseDown);
            } else if (event.type === 'mouseup') {
                console.log('[AudioInteractionService]: Playing button mouse-up sound')
                this.play(this.interactions.mouse.mouseUp);
            }
        });
    }

    listenForTouchInteractions(): void {
        // Use "capture" so this service hears the event even if propagation is stopped
        const touchStart$ = fromEvent<MouseEvent>(document.body, 'touchstart', {capture: true});
        const touchEnd$ = fromEvent<MouseEvent>(document.body, 'touchend', {capture: true});
        const touchEvents$ = merge(touchStart$, touchEnd$);

        touchEvents$.pipe(
            filter(() => !!this.interactions && !!this.interactions.touch),
            takeUntil(merge(this.stop$, this.destroyed$))
        ).subscribe(event => {
            if (event.type === 'touchstart') {
                console.log('[AudioInteractionService]: Playing button touch-start sound');
                this.play(this.interactions.touch.touchStart);
            } else if (event.type === 'touchend') {
                console.log('[AudioInteractionService]: Playing button touch-end sound')
                this.play(this.interactions.touch.touchEnd);
            }
        });
    }

    listenForDialogInteractions(): void {
        this.dialogService.beforeOpened$
            .pipe(
                tap(() => console.log('[AudioInteractionService]: Playing dialog opening sound')),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(() => this.play(this.interactions.dialog.opening));

        this.dialogService.beforeClosed$
            .pipe(
                tap(() => console.log('[AudioInteractionService]: Playing dialog closing sound')),
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(() => this.play(this.interactions.dialog.closing));
    }

    onAudioConfig(config: AudioConfig): void {
        console.log('[AudioInteractionService]: Configuration updated', config);

        if (this.enabled && !config.interactions.enabled) {
            this.stopListening();
        }

        if (!this.enabled && config.interactions.enabled) {
            this.listen();
        }

        this.enabled = config.interactions.enabled;
        this.interactions = config.interactions;
    }

    play(audioRequest: AudioRequest): Observable<HTMLAudioElement> {
        return this.audioService.play(audioRequest);
    }
}