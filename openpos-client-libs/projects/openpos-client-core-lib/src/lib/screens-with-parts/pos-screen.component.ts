import { Logger } from '../core/services/logger.service';
import { AppInjector } from '../core/app-injector';
import { IAbstractScreen } from '../core/interfaces/abstract-screen.interface';
import { IScreen } from '../shared/components/dynamic-screen/screen.interface';
import { SessionService } from '../core/services/session.service';
import { deepAssign } from '../utilites/deep-assign';
import { IActionItem } from '../core/interfaces/action-item.interface';

/**
 * @ignore
 */
export abstract class PosScreen<T extends IAbstractScreen> implements IScreen {

    screen: T;
    session: SessionService;
    log: Logger;

    constructor() {
        this.session = AppInjector.Instance.get(SessionService);
        this.log = AppInjector.Instance.get(Logger);
    }

    show(screen: any) {
        // Check for old screen message versions
        if ( screen.hasOwnProperty('template')) {
            this.log.warn(`Received a message for deprecated version of screen \'${screen.screenType}\'`);
        }
        this.screen = deepAssign(this.screen, screen);
        this.buildScreen();
    }

    onMenuItemClick( menuItem: IActionItem, payload?: any) {
        if (menuItem.enabled) {
            this.session.onAction( menuItem, payload );
        }
    }

    abstract buildScreen();
}
