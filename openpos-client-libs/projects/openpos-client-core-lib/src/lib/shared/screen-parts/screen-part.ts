import {Injector, OnDestroy, OnInit, Optional} from '@angular/core';
import {Observable, Subject, Subscription} from 'rxjs';
import {filter} from 'rxjs/operators';
import {MessageProvider} from '../providers/message.provider';
import {IActionItem} from '../../core/actions/action-item.interface';
import {SessionService} from '../../core/services/session.service';
import {deepAssign} from '../../utilites/deep-assign';
import {getValue} from '../../utilites/object-utils';
import {MediaBreakpoints, OpenposMediaService} from '../../core/media/openpos-media.service';
import {UIMessage} from '../../core/messages/ui-message';
import {LifeCycleMessage} from '../../core/messages/life-cycle-message';
import {LifeCycleEvents} from '../../core/messages/life-cycle-events.enum';
import {LifeCycleTypeGuards} from '../../core/life-cycle-interfaces/lifecycle-type-guards';
import {MessageTypes} from '../../core/messages/message-types';
import {ActionService} from '../../core/actions/action.service';
import {KeyPressProvider} from '../providers/keypress.provider';

export abstract class ScreenPartComponent<T> implements OnDestroy, OnInit {
    destroyed$ = new Subject();
    beforeScreenDataUpdated$ = new Subject<T>();
    afterScreenDataUpdated$ = new Subject<T>();
    sessionService: SessionService;
    screenPartName: string;
    screenData: T;
    messageProvider: MessageProvider;
    mediaService: OpenposMediaService;
    actionService: ActionService;
    keyPressProvider: KeyPressProvider;
    isMobile$: Observable<boolean>;

    public subscriptions = new Subscription();

    // I don't completely understand why we need @Optional here. I suspect it has something to do with
    // creating these components dynamically and this being an abstract class.
    // This is not optional
    constructor( @Optional() injector: Injector) {
        // This should never happen, but just incase lets make sure its not null or undefined
        if ( !!injector ) {
            this.sessionService = injector.get(SessionService);
            this.mediaService = injector.get(OpenposMediaService);
            this.messageProvider = injector.get(MessageProvider);
            this.actionService = injector.get(ActionService);
            this.keyPressProvider = injector.get(KeyPressProvider);
        }
        const sizeMap = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, false],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]);
        this.isMobile$ = this.mediaService.observe(sizeMap);
    }

    ngOnInit(): void {
        console.log('[ ==========    ngOnInit: screen-part    ========== ]');
        this.subscriptions.add(this.messageProvider.getScopedMessages$<UIMessage>()
            .pipe(filter(s => s.screenType !== 'Loading')).subscribe(s => {
                const screenPartData = getValue(s, this.screenPartName);
                console.log('[ ====> ] screenPartName: ' + this.screenPartName);
                console.log('[ ====> ] s');
                console.dir(s);
                console.log('[ ====> ] screenPartData');
                console.dir(screenPartData);
                if (screenPartData !== undefined && screenPartData !== null) {
                    this.screenData = deepAssign(this.screenData, screenPartData);

                    //Would be better if ScreenPart interfaces were constrained to an abstract ScreenPart class
                    //that contains this but I don't want to force everyone to update their screen part objects.
                    this.screenData['willUnblock'] = s.willUnblock;
                } else {
                    this.screenData = deepAssign(this.screenData, s);
                }
                console.log('[ SCREEN PART DATA DEEP ASSIGN ] this.screenData:');
                console.dir(this.screenData);
                this.beforeScreenDataUpdated$.next(this.screenData);
                this.screenDataUpdated();
                this.afterScreenDataUpdated$.next(this.screenData);
            }));
        console.log('[ ================================================= ]');
        this.subscriptions.add(this.messageProvider.getAllMessages$().pipe(
            filter( message => message.type === MessageTypes.LIFE_CYCLE_EVENT )
        ).subscribe( message => this.handleLifeCycleEvent(message as LifeCycleMessage)));

        this.subscriptions.add(this.messageProvider.getAllMessages$().pipe(
            filter( message => message.type === MessageTypes.SCREEN_VALUE_UPDATE )
        ).subscribe( message => this.handleScreenValueUpdate(message)));
    }
    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
        this.destroyed$.next();
    }

    doAction( action: IActionItem | string, payload?: any) {
        if ( typeof(action) === 'string' ) {
            this.actionService.doAction( {action}, payload);
        } else {
            this.actionService.doAction(action, payload);
        }
    }

    isActionDisabled(action: string): Observable<boolean> {
        return this.actionService.actionIsDisabled$(action);
    }

    private handleLifeCycleEvent( message: LifeCycleMessage ) {
        switch ( message.eventType ) {
            case LifeCycleEvents.BecomingActive:
                if ( LifeCycleTypeGuards.handlesBecomingActive(this) ) {
                    this.onBecomingActive();
                }
                break;
            case LifeCycleEvents.LeavingActive:
                if ( LifeCycleTypeGuards.handlesLeavingActive(this) ) {
                    this.onLeavingActive();
                }
                break;
        }
    }

    private handleScreenValueUpdate( message: any ) {
        if (this.screenData[message.valuePath] !== undefined) {
            this.screenData[message.valuePath] = message.value;
        }
    }

    abstract screenDataUpdated();
}
