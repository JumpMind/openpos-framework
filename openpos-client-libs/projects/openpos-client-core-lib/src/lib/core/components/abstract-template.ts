import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { AppInjector } from '../app-injector';
import { ViewChild, ComponentRef, ComponentFactory } from '@angular/core';
import { ScreenDirective } from '../../shared/directives/screen.directive';
import { SessionService } from '../services/session.service';
import { Logger } from '../services/logger.service';

export abstract class AbstractTemplate<T> implements IScreen {

    @ViewChild(ScreenDirective) host: ScreenDirective;
    private currentScreenRef: ComponentRef<IScreen>;

    private actionDisablers = new Map<string, actionShouldBeDisabled>();
    template: T;

    session: SessionService;
    log: Logger;

    constructor() {
        this.session = AppInjector.Instance.get(SessionService);
        this.log = AppInjector.Instance.get(Logger);
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
