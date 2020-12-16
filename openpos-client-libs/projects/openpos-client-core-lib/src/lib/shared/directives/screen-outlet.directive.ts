import {
    ComponentRef,
    Directive,
    ViewContainerRef,
    OnInit,
    OnDestroy,
    Output,
    EventEmitter,
    Input,
    Renderer2
} from '@angular/core';
import { OverlayContainer } from '@angular/cdk/overlay';
import { Subscription } from 'rxjs';
import { AbstractTemplate } from '../../core/components/abstract-template';
import { Logger } from '../../core/services/logger.service';
import { ScreenService } from '../../core/services/screen.service';
import { SessionService } from '../../core/services/session.service';
import { ConfigurationService } from '../../core/services/configuration.service';
import { DialogService } from '../../core/services/dialog.service';
import { MessageTypes } from '../../core/messages/message-types';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';
import { LifeCycleTypeGuards } from '../../core/life-cycle-interfaces/lifecycle-type-guards';
import { IScreen } from '../components/dynamic-screen/screen.interface';
import { FocusService } from '../../core/focus/focus.service';
import { PersonalizationService } from '../../core/personalization/personalization.service';
import { filter } from 'rxjs/operators';
import { ScreenCreatorService } from '../../core/services/screen-creator.service';

// tslint:disable-next-line:directive-selector
@Directive({ selector: '[openposScreenOutlet]' })
export class OpenposScreenOutletDirective implements OnInit, OnDestroy {

    private componentRef: ComponentRef<any> | null = null;
    @Output() componentEmitter = new EventEmitter<{ componentRef: ComponentRef<any>, screen: any }>();
    @Input() unsubscribe = true;

    public templateTypeName: string;
    public screenTypeName: string;
    private screenName: string;
    private screenId: string;

    public classes = '';

    public currentTheme: string;

    private installedScreen: IScreen;
    private installedTemplate: AbstractTemplate<any>;
    private subscriptions = new Subscription();

    constructor(
        private log: Logger,
        public screenService: ScreenService,
        private viewContainerRef: ViewContainerRef,
        public session: SessionService,
        public configurationService: ConfigurationService,
        public overlayContainer: OverlayContainer,
        private dialogService: DialogService,
        private focusService: FocusService,
        private screenCreator: ScreenCreatorService,
        public renderer: Renderer2,
        private personaliation: PersonalizationService) {
    }

    ngOnInit(): void {
        this.updateTemplateAndScreen();
        this.subscriptions.add(this.session.getMessages('Screen').pipe(filter(m => m.screenType !== 'NoOp'))
            .subscribe((message) => this.handle(message)));
        this.subscriptions.add(this.session.getMessages('Connected').subscribe((message) => this.handle(new SplashScreen())));
        this.subscriptions.add(this.session.getMessages(
            MessageTypes.LIFE_CYCLE_EVENT).subscribe(message => this.handleLifeCycleEvent(message)));
        this.subscriptions.add(this.configurationService.theme$.subscribe(theme => {
            this.updateTheme(theme);
        }));
    }

    ngOnDestroy(): void {
        if (this.unsubscribe === true) {
            this.session.unsubscribe();
        }
        if (this.componentRef) {
            this.componentRef.destroy();
        }

        if (!!this.subscriptions) {
            this.subscriptions.unsubscribe();
        }
    }

    handle(message: any) {
        if (message.screenType !== 'NoOp') {
            this.updateTemplateAndScreen(message);
        }
    }

    private handleLifeCycleEvent(message: LifeCycleMessage) {
        switch (message.eventType) {
            case LifeCycleEvents.DialogClosing:
                if (LifeCycleTypeGuards.handlesBecomingActive(this.componentRef.instance)) {
                    this.componentRef.instance.onBecomingActive();
                }
                break;
            case LifeCycleEvents.DialogOpening:
                if (LifeCycleTypeGuards.handlesLeavingActive(this.componentRef.instance)) {
                    this.componentRef.instance.onLeavingActive();
                }
                break;
        }
    }

    protected async updateTemplateAndScreen(screen?: any) {
        if (!screen) {
            screen = new SplashScreen();
        }

        if (this.dialogService.isDialogOpen()) {
            // Close any open dialogs
            await this.dialogService.closeDialog();
            this.focusService.restoreInitialFocus();
        }

        // Cancel the loading message
        this.session.cancelLoading();

        let trap = false;
        const original = document.activeElement as HTMLElement;

        if (screen &&
            (screen.refreshAlways
                || screen.screenType !== this.screenTypeName
                || screen.name !== this.screenName
                || screen.id !== this.screenId)
        ) {
            this.logSwitchScreens(screen);

            this.screenName = screen.name;
            this.screenId = screen.id;
            let screenToCreate = this.screenTypeName = screen.screenType;
            // If we have a template we'll create that instead of the screen and
            // later we'll install the screen in the temlate
            if (screen.template) {
                screenToCreate = this.templateTypeName = screen.template.type;
            }

            this.viewContainerRef.clear();
            if (!!this.componentRef) {
                this.componentRef.destroy();
            }

            this.focusService.destroy();

            this.componentRef = null;
            this.installedScreen = null;
            this.installedTemplate = null;

            // Create our screen component
            const componentFactory = this.screenService.resolveScreen(screenToCreate, this.currentTheme);
            this.componentRef = this.screenCreator.createScreenComponent(componentFactory, this.viewContainerRef);
            this.updateTheme(this.currentTheme);

            // If we accept an inner screen meaning we are a template, install the screen
            if (this.componentRef.instance.installScreen) {
                this.installedTemplate = this.componentRef.instance as AbstractTemplate<any>;
                this.installedScreen = this.installedTemplate.installScreen(this.screenService.resolveScreen(
                    this.screenTypeName, this.currentTheme)) as IScreen;
            }

            trap = true;
        }

        if ( this.componentRef.instance.initialize ) {
            this.componentRef.instance.initialize( this.componentRef.injector );
        }

        if (this.componentRef.instance.show) {
            this.componentRef.instance.show(screen);
        }

        if (this.installedScreen) {
            this.installedScreen.show(screen, this.installedTemplate);
        }

        if (trap) {
            // If this screen was just created, focus the first element
            this.focusService.createInitialFocus(this.componentRef.location.nativeElement);
        } else {
            // If this screen was updated, focus the previously focused element
            setTimeout(() => {
                // Get an updated element in the case where the screen/form has been refreshed
                const updatedElement = document.getElementById(original.id);
                this.focusService.restoreFocus(updatedElement);
            });
        }

        this.updateClasses(screen);

        // Output the componentRef and screen to the training-wrapper
        this.componentEmitter.emit({ componentRef: this.componentRef, screen });
        this.session.sendMessage( new LifeCycleMessage(LifeCycleEvents.ScreenUpdated, screen));
    }

    protected updateTheme(theme: string) {
        this.log.info('updating theme to ' + theme);
        this.overlayContainer.getContainerElement().classList.remove(this.currentTheme);
        this.overlayContainer.getContainerElement().classList.remove('default-theme');
        this.overlayContainer.getContainerElement().classList.add(theme);
        if (!!this.componentRef) {
            const parent = this.renderer.parentNode(this.componentRef.location.nativeElement);
            this.renderer.removeClass(parent, this.currentTheme);
            this.renderer.removeClass(parent, 'default-theme');
            this.renderer.addClass(parent, theme);
            // Add a class for each personalization param: personalization-paramname-paramvalue
            const personalizationParams = this.personaliation.getPersonalizationProperties();
            for (const [key, value] of personalizationParams) {
                this.renderer.addClass(parent, `personalization-${key.toLowerCase()}-${value.toLowerCase()}`);
            }
        }
        this.currentTheme = theme;
    }

    protected logSwitchScreens(screen: any) {
        let msg = `>>> Switching screens from "${this.screenTypeName}" to "${screen.screenType}"`;
        let nameLogged = false;
        let sequenceLogged = false;
        if (screen.name && screen.name !== screen.screenType) {
            nameLogged = true;
            msg += ` (name "${screen.name}"`;
        }
        if (screen.sequenceNumber) {
            sequenceLogged = true;
            if (!nameLogged) {
                msg += ` (`;
            } else {
                msg += `, `;
            }
            msg += `sequence ${screen.sequenceNumber})`;
        }
        if (nameLogged && !sequenceLogged) {
            msg += `)`;
        }

        this.log.info(msg);
    }

    protected updateClasses(screen: any) {
        if (screen) {
            // remove old classes
            if (this.classes) {
                this.classes.split(' ').forEach(c => this.renderer.removeClass(this.componentRef.location.nativeElement, c));
            }
            this.classes = '';
            switch (this.session.getAppId()) {
                case 'pos':
                    if (screen.screenType === 'Home') {
                        this.classes = 'pos main-background';
                    } else {
                        this.classes = 'pos';
                    }
                    break;
                case 'selfcheckout':
                    this.classes = 'selfcheckout';
                    break;
                case 'customerdisplay':
                    this.classes = 'selfcheckout';
                    break;
            }

            // Add new classes
            if (this.classes) {
                this.classes.split(' ').forEach(c => this.renderer.addClass(this.componentRef.location.nativeElement, c));
            }
        }
    }
}

export class SplashScreen {
    type = 'Screen';
    screenType = 'SplashScreen';
}
