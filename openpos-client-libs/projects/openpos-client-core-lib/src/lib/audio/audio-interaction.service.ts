import { Injectable, OnDestroy } from '@angular/core';
import { fromEvent, merge, Subject } from 'rxjs';
import { filter, takeUntil, tap } from 'rxjs/operators';
import { SessionService } from '../core/services/session.service';
import { MessageTypes } from '../core/messages/message-types';
import { AudioConfigMessage } from './audio-config-message.interface';
import { AudioInteractionSet } from './audio-interaction-set.interface';
import { AudioRequest } from './audio.request.interface';
import { AudioService } from './audio.service';
import { DialogService } from '../core/services/dialog.service';

@Injectable({
    providedIn: 'root',
})
export class AudioInteractionService implements OnDestroy {
    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private enabled = true;
    private interactions: AudioInteractionSet

    constructor(private sessionService: SessionService, private dialogService: DialogService, private audioService: AudioService) {
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listen(): void {
        console.log('[AudioInteractionService]: Listening to the user...');

        this.listenForMouseInteractions();
        this.listenForDialogInteractions();

        this.sessionService.getMessages(MessageTypes.AUDIO_CONFIG)
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(message => this.onAudioConfigMessage(message));
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
                console.log('[AudioInteractionService]: Playing button click-in sound');
                this.play(this.interactions.mouse.clickIn);
            } else if (event.type === 'mouseup') {
                console.log('[AudioInteractionService]: Playing button click-out sound')
                this.play(this.interactions.mouse.clickOut);
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

    onAudioConfigMessage(message: AudioConfigMessage): void {
        console.log('[AudioInteractionService]: Received config message', message);

        if (this.enabled && !message.interactions.enabled) {
            this.stopListening();
        }

        if (!this.enabled && message.interactions.enabled) {
            this.listen();
        }

        this.enabled = message.interactions.enabled;
        this.interactions = message.interactions;
    }

    play(audioRequest: AudioRequest): void {
        this.audioService.play(audioRequest);
    }
}