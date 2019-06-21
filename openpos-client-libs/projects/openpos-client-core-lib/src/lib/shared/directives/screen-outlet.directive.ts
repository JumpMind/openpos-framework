import {
    ComponentRef,
    Directive,
    ViewContainerRef,
    OnInit,
    OnDestroy,
    Output,
    EventEmitter,
    Input,
    Renderer2,
    ElementRef
} from '@angular/core';
import { OverlayContainer } from '@angular/cdk/overlay';
import { Subscription } from 'rxjs';
import { IScreen } from '../../core/components/dynamic-screen/screen.interface';
import { AbstractTemplate } from '../../core/components/abstract-template';
import { Logger } from '../../core/services/logger.service';
import { ScreenService } from '../../core/services/screen.service';
import { SessionService } from '../../core/services/session.service';
import { ConfigurationService } from '../../core/services/configuration.service';
import { DialogService } from '../../core/services/dialog.service';
import { FocusTrapFactory, FocusTrap } from '@angular/cdk/a11y';
import { MessageTypes } from '../../core/messages/message-types';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';
import { LifeCycleTypeGuards } from '../../core/life-cycle-interfaces/lifecycle-type-guards';

// tslint:disable-next-line:directive-selector
@Directive({ selector: '[openposScreenOutlet]' })
export class OpenposScreenOutletDirective implements OnInit, OnDestroy {

    private componentRef: ComponentRef<any> | null = null;
    @Output() componentEmitter = new EventEmitter<{ componentRef: ComponentRef<any>, screen: any }>();
    @Input() unsubscribe = true;

    public templateTypeName: string;
    public screenTypeName: string;
    private screenName: string;

    public classes = '';

    public currentTheme: string;

    private installedScreen: IScreen;
    private installedTemplate: AbstractTemplate<any>;
    private subscriptions = new Subscription();
    private focusTrap: FocusTrap;

    constructor(
        private log: Logger,
        public screenService: ScreenService,
        private viewContainerRef: ViewContainerRef,
        public session: SessionService,
        public configurationService: ConfigurationService,
        public overlayContainer: OverlayContainer,
        private dialogService: DialogService,
        private focusTrapFactory: FocusTrapFactory,
        public renderer: Renderer2) {
    }

    ngOnInit(): void {
        this.updateTemplateAndScreen();
        this.subscriptions.add(this.session.getMessages('Screen').subscribe((message) => this.handle(message)));
        this.subscriptions.add(this.session.getMessages('Connected').subscribe((message) => this.handle(new SplashScreen())));
        this.subscriptions.add(this.session.getMessages(
            MessageTypes.LIFE_CYCLE_EVENT).subscribe( message => this.handleLifeCycleEvent(message)));
        this.subscriptions.add(this.configurationService.theme$.subscribe( theme => {
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

    private handleLifeCycleEvent( message: LifeCycleMessage ) {
        switch ( message.eventType ) {
            case LifeCycleEvents.DialogClosing:
                if ( LifeCycleTypeGuards.handlesBecomingActive(this.componentRef.instance) ) {
                    this.componentRef.instance.onBecomingActive();
                }
                break;
            case LifeCycleEvents.DialogOpening:
                if ( LifeCycleTypeGuards.handlesLeavingActive(this.componentRef.instance) ) {
                    this.componentRef.instance.onLeavingActive();
                }
                break;
        }
    }

    protected async updateTemplateAndScreen(screen?: any) {
        if (!screen) {
            screen = new SplashScreen();
        }

        if ( this.dialogService.isDialogOpen ) {
            // Close any open dialogs
            await this.dialogService.closeDialog();
        }

        // Cancel the loading message
        this.session.cancelLoading();

        let trap = false;
        const original = document.activeElement as HTMLElement;

        if (screen &&
            (screen.refreshAlways
                || screen.screenType !== this.screenTypeName
                || screen.name !== this.screenName)
        ) {
            this.logSwitchScreens(screen);

            this.screenName = screen.name;
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

            if (!!this.focusTrap) {
                this.focusTrap.destroy();
            }

            this.focusTrap = null;
            this.componentRef = null;
            this.installedScreen = null;
            this.installedTemplate = null;

            // Create our screen component
            const componentFactory = this.screenService.resolveScreen(screenToCreate, this.currentTheme);
            this.componentRef = this.viewContainerRef.createComponent(componentFactory,
                this.viewContainerRef.length, this.viewContainerRef.parentInjector);
            this.updateTheme(this.currentTheme);

            // If we accept an inner screen meaning we are a template, install the screen
            if (this.componentRef.instance.installScreen) {
                this.installedTemplate = this.componentRef.instance as AbstractTemplate<any>;
                this.installedScreen = this.installedTemplate.installScreen(this.screenService.resolveScreen(
                    this.screenTypeName, this.currentTheme)) as IScreen;
            }

            trap = true;
        }

        if (this.componentRef.instance.show) {
            this.componentRef.instance.show(screen);
        }

        if (this.installedScreen) {
            this.installedScreen.show(screen, this.installedTemplate);
        }

        if (trap) {
            // If this screen was just created, focus the first element
            this.focusTrap = this.focusTrapFactory.create(this.componentRef.location.nativeElement);
            this.focusTrap.focusInitialElementWhenReady();
        } else {
            // If this screen was updated, focus the previously focused element
            setTimeout(() => {
                const updatedElement = document.getElementById(original.id);
                if (updatedElement) {
                    updatedElement.focus();
                }
            });
        }

        this.updateClasses(screen);

        // Output the componentRef and screen to the training-wrapper
        this.componentEmitter.emit({ componentRef: this.componentRef, screen });

    }

    protected updateTheme(theme: string) {
        this.log.info('updating theme to ' + theme);
        this.overlayContainer.getContainerElement().classList.remove(this.currentTheme);
        this.overlayContainer.getContainerElement().classList.remove('default-theme');
        this.overlayContainer.getContainerElement().classList.add(theme);
        if ( !!this.componentRef ) {
            const parent = this.renderer.parentNode(this.componentRef.location.nativeElement);
            this.renderer.removeClass(parent, this.currentTheme);
            this.renderer.removeClass(parent, 'default-theme');
            this.renderer.addClass(parent, theme);
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
