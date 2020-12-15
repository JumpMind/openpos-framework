import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { AppInjector } from '../app-injector';
import { ViewChild, ComponentRef, ComponentFactory, Injector } from '@angular/core';
import { ScreenDirective } from '../../shared/directives/screen.directive';
import { SessionService } from '../services/session.service';
import { Logger } from '../services/logger.service';
import { ActionService } from '../actions/action.service';

export abstract class AbstractTemplate<T> implements IScreen {

    @ViewChild(ScreenDirective) host: ScreenDirective;
    private currentScreenRef: ComponentRef<IScreen>;

    private actionDisablers = new Map<string, actionShouldBeDisabled>();
    template: T;

    session: SessionService;
    actionService: ActionService;
    log: Logger;

    constructor(injector: Injector) {
        this.session = injector.get(SessionService);
        this.log = injector.get(Logger);
        this.actionService = injector.get(ActionService);
    }

    public installScreen(screenComponentFactory: ComponentFactory<IScreen>): IScreen {
        const viewContainerRef = this.host.viewContainerRef;
        viewContainerRef.clear();
        if (this.currentScreenRef) {
            this.currentScreenRef.destroy();
        }

        this.currentScreenRef = viewContainerRef.createComponent(screenComponentFactory);
        return this.currentScreenRef.instance;
    }

    ngOnDestry(): void {
        if (this.currentScreenRef) {
            this.currentScreenRef.destroy();
        }
    }

    show(screen: any) {
        this.template = screen.template;
        this.buildTemplate();
    }

    abstract buildTemplate();

    // tslint:disable-next-line:no-shadowed-variable
    registerActionDisabler(action: string, actionShouldBeDisabled) {
        this.actionDisablers.set(action, actionShouldBeDisabled);
    }

    actionIsDisabled(action: string): boolean {
        if (this.actionDisablers.has(action)) {
            return this.actionDisablers.get(action)();
        }

        return false;
    }
}

export type actionShouldBeDisabled = () => boolean;
