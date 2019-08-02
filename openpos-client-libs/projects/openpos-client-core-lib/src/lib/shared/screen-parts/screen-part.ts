import { OnDestroy, OnInit, Injector, Optional } from '@angular/core';
import { Subscription, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { MessageProvider } from '../providers/message.provider';
import { IActionItem } from '../../core/actions/action-item.interface';
import { SessionService } from '../../core/services/session.service';
import { Logger } from '../../core/services/logger.service';
import { deepAssign } from '../../utilites/deep-assign';
import { getValue } from '../../utilites/object-utils';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { UIMessage } from '../../core/messages/ui-message';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';
import { LifeCycleTypeGuards } from '../../core/life-cycle-interfaces/lifecycle-type-guards';
import { MessageTypes } from '../../core/messages/message-types';
import { ActionService } from '../../core/actions/action.service';

export abstract class ScreenPartComponent<T> implements OnDestroy, OnInit {

    sessionService: SessionService;
    log: Logger;
    screenPartName: string;
    screenData: T;
    messageProvider: MessageProvider;
    mediaService: OpenposMediaService;
    actionService: ActionService;
    isMobile$: Observable<boolean>;
    initialScreenType = '';
    public subscriptions = new Subscription();

    // I don't completely understand why we need @Optional here. I suspect it has something to do with
    // creating these components dynamically and this being an abstract class.
    // This is not optional
    constructor( @Optional() injector: Injector) {
        // This should never happen, but just incase lets make sure its not null or undefined
        if ( !!injector ) {
            this.sessionService = injector.get(SessionService);
            this.log = injector.get(Logger);
            this.mediaService = injector.get(OpenposMediaService);
            this.messageProvider = injector.get(MessageProvider);
            this.actionService = injector.get(ActionService);
        }
        const sizeMap = new Map([
            ['xs', true],
            ['sm', false],
            ['md', false],
            ['lg', false],
            ['xl', false]
        ]);
        this.isMobile$ = this.mediaService.mediaObservableFromMap(sizeMap);
    }

    ngOnInit(): void {
        this.subscriptions.add(this.messageProvider.getScopedMessages$<UIMessage>()
            .pipe(filter(s => s.screenType !== 'Loading')).subscribe(s => {
                // We want to save off the type of the first screen we got data from and
                // only get data from screens of this type in the furture. This will prevent
                // getting data from the next screen if we are not already cleaned up.
                if (!this.initialScreenType.length) {
                    this.initialScreenType = s.screenType;
                }
                if (s.screenType === this.initialScreenType) {
                    const screenPartData = getValue(s, this.screenPartName);
                    if (screenPartData !== undefined && screenPartData !== null) {
                        this.screenData = deepAssign(this.screenData, screenPartData);
                    } else {
                        this.screenData = deepAssign(this.screenData, s);
                    }
                    this.screenDataUpdated();
                }
            }));
        this.subscriptions.add(this.messageProvider.getAllMessages$().pipe(
            filter( message => message.type === MessageTypes.LIFE_CYCLE_EVENT )
        ).subscribe( message => this.handleLifeCycleEvent(message as LifeCycleMessage)));
    }
    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
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

    abstract screenDataUpdated();
}
