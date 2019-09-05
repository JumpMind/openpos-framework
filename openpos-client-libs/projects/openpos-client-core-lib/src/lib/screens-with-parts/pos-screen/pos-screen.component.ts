import { Logger } from '../../core/services/logger.service';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { deepAssign } from '../../utilites/deep-assign';
import { IActionItem } from '../../core/actions/action-item.interface';
import { Injector, OnDestroy, Optional } from '@angular/core';
import { ActionService } from '../../core/actions/action.service';
import { Subscription } from 'rxjs';

export abstract class PosScreen<T extends IAbstractScreen> implements IScreen, OnDestroy {


    screen: T;
    log: Logger;
    actionService: ActionService;

    subscriptions = new Subscription();

    // I don't completely understand why we need @Optional here. I suspect it has something to do with
    // creating these components dynamically and this being an abstract class.
    constructor( @Optional() injector: Injector) {
        // This should never happen, but just incase lets make sure its not null or undefined
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

    abstract buildScreen();
}
