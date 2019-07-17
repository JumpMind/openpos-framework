import { Logger } from './../../core/services/logger.service';
import { AppInjector } from '../../core/app-injector';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { deepAssign } from '../../utilites/deep-assign';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { Injector } from '@angular/core';
import { ActionService } from '../../core/services/action.service';

export abstract class PosScreen<T extends IAbstractScreen> implements IScreen {

    screen: T;
    log: Logger;
    actionService: ActionService;

    constructor( injector: Injector) {
        this.log = injector.get(Logger);
        this.actionService = injector.get(this.actionService);
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

    abstract buildScreen();
}
