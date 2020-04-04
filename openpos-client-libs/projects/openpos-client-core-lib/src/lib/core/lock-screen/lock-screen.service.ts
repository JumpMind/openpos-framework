import {Overlay, OverlayRef} from '@angular/cdk/overlay';
import {ComponentPortal, PortalInjector} from '@angular/cdk/portal';
import {Injectable, InjectionToken, Injector} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Observable, ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {MessageProvider} from '../../shared/providers/message.provider';
import {ActionService} from '../actions/action.service';
import {LockScreenMessage} from '../messages/lock-screen-message';
import {MessageTypes} from '../messages/message-types';
import {SessionService} from '../services/session.service';
import {LockScreenComponent} from './lock-screen.component';

export const LOCK_SCREEN_DATA = new InjectionToken<Observable<LockScreenMessage>>('LOCK_SCREEN_DATA');

@Injectable({
    providedIn: 'root'
})
export class LockScreenService {

    private lockScreenOverlayRef: OverlayRef;
    private lockScreenData = new ReplaySubject<LockScreenMessage>();

    constructor( sessionService: SessionService,
                 private overlay: Overlay,
                 private injector: Injector) {
        sessionService.getMessages(MessageTypes.LOCK_SCREEN).pipe(
            tap( () => this.showLockScreen() ),
            tap( message => this.lockScreenData.next(message))
        ).subscribe();
        sessionService.getMessages(MessageTypes.UNLOCK_SCREEN).pipe(
            tap( () => this.removeLockScreen())
        ).subscribe();
    }

    private showLockScreen(){
        if( this.lockScreenOverlayRef != null) return;

        this.lockScreenOverlayRef = this.overlay.create({
            height: '100%',
            width: '100%'
        });
        const lockScreenPortal = new ComponentPortal(LockScreenComponent, null, this.createInjector());
        this.lockScreenOverlayRef.attach(lockScreenPortal);
    }

    private removeLockScreen(){
        this.lockScreenOverlayRef.dispose();
        this.lockScreenOverlayRef = null;
    }

    private createInjector(): PortalInjector {
        // Instantiate new WeakMap for our custom injection tokens
        const injectionTokens = new WeakMap();

        // Set custom injection tokens
        injectionTokens.set(LOCK_SCREEN_DATA, this.lockScreenData);
        const componentInjector = Injector.create(
            {   providers: [ { provide: ActionService, useClass: ActionService, deps: [MatDialog, MessageProvider] }],
                parent: this.injector }
        );
        // Instantiate new PortalInjector
        return new PortalInjector(componentInjector, injectionTokens);
    }

}