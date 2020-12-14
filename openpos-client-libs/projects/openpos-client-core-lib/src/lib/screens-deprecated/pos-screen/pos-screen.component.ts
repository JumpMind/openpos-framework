import { Logger } from './../../core/services/logger.service';
import { AppInjector } from '../../core/app-injector';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { SessionService } from '../../core/services/session.service';
import { deepAssign } from '../../utilites/deep-assign';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { ActionService } from '../../core/actions/action.service';
import { Injector, OnDestroy, Optional } from '@angular/core';
import { Subscription } from 'rxjs';
/**
 * @ignore
 */
export abstract class PosScreen<T extends IAbstractScreen> implements IScreen, OnDestroy {

    screen: T;
    log: Logger;
    actionService: ActionService;

    subscriptions = new Subscription();

    constructor( @Optional() injector: Injector) {
        if ( !!injector ) {
            this.log = injector.get(Logger);
            this.actionService = injector.get(ActionService);
        }
    }

    show(screen: any) {
        this.screen = deepAssign(this.screen, screen);
        this.buildScreen();
    }

    doAction( action: IActionItem | string, payload?: any) {
        if ( typeof(action) === 'string' ) {
            this.actionService.doAction( {action}, payload);
        } else {
            this.actionService.doAction(action, payload);
        }
    }

    ngOnDestroy(): void {
        if ( this.subscriptions ) {
            this.subscriptions.unsubscribe();
        }
    }
/*
    onMenuItemClick( menuItem: IActionItem, payload?: any) {
        if (menuItem.enabled) {
            this.session.onAction( menuItem, payload );
        }
    }
*/
    abstract buildScreen();
}
