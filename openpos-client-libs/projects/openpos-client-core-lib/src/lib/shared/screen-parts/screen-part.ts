import { OnDestroy, OnInit } from '@angular/core';
import { Subscription, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { MessageProvider } from '../providers/message.provider';
import { AppInjector } from '../../core/app-injector';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SessionService } from '../../core/services/session.service';
import { Logger } from '../../core/services/logger.service';
import { deepAssign } from '../../utilites/deep-assign';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { UIMessage } from '../../core/messages/ui-message';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';
import { LifeCycleTypeGuards } from '../../core/life-cycle-interfaces/lifecycle-type-guards';
import { MessageTypes } from '../../core/messages/message-types';

export abstract class ScreenPartComponent<T> implements OnDestroy, OnInit {

    sessionService: SessionService;
    log: Logger;
    screenPartName: string;
    screenData: T;
    messageProvider: MessageProvider;
    mediaService: OpenposMediaService;
    isMobile$: Observable<boolean>;
    initialScreenType = '';
    public subscriptions = new Subscription();

    constructor(messageProvider: MessageProvider) {
        this.sessionService = AppInjector.Instance.get(SessionService);
        this.log = AppInjector.Instance.get(Logger);
        this.mediaService = AppInjector.Instance.get(OpenposMediaService);

        this.messageProvider = messageProvider;
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
                if (!this.initialScreenType.length) {
                    this.initialScreenType = s.screenType;
                }
                if (s.screenType === this.initialScreenType) {
                    if (s.hasOwnProperty(this.screenPartName)) {
                        this.screenData = deepAssign(this.screenData, s[this.screenPartName]);
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

    onMenuItemClick(menuItem: IActionItem, payload?: any) {
        if (menuItem.enabled) {
            this.sessionService.onAction(menuItem, payload);
        }
    }

    isActionDisabled(action: string): Observable<boolean> {
        return this.sessionService.actionIsDisabled(action);
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
