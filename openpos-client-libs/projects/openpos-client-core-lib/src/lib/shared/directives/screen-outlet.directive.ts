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
import { Logger } from '../../core/services/logger.service';
import { ScreenService } from '../../core/services/screen.service';
import { SessionService } from '../../core/services/session.service';
import { ConfigurationService } from '../../core/services/configuration.service';
import { DialogService } from '../../core/services/dialog.service';
import { MessageTypes } from '../../core/messages/message-types';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';
import { LifeCycleTypeGuards } from '../../core/life-cycle-interfaces/lifecycle-type-guards';
import { ScreenCreatorService } from '../../core/services/screen-creator.service';
import { FocusService } from '../../core/focus/focus.service';

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
        public renderer: Renderer2) {
    }

    ngOnInit(): void {
        this.updateScreen();
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
            this.updateScreen(message);
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

    protected async updateScreen(screen?: any) {
        if (!screen) {
            screen = new SplashScreen();
        }

        if ( this.dialogService.isDialogOpen ) {
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
                || screen.name !== this.screenName)
        ) {
            this.logSwitchScreens(screen);

            this.screenName = screen.name;
            this.screenTypeName = screen.screenType;

            this.viewContainerRef.clear();
            if (!!this.componentRef) {
                this.componentRef.destroy();
            }

            this.focusService.destroy();
            this.componentRef = null;

            // Create our screen component
            const componentFactory = this.screenService.resolveScreen(this.screenTypeName, this.currentTheme);
            this.componentRef = this.screenCreator.createScreenComponent(componentFactory, this.viewContainerRef );
            this.updateTheme(this.currentTheme);

            trap = true;
        }

        if ( this.componentRef.instance.initialize ) {
            this.componentRef.instance.initialize( this.componentRef.injector );
        }

        if (this.componentRef.instance.show) {
            this.componentRef.instance.show(screen);
        }

        if (trap) {
            // If this screen was just created, focus the first element
            this.focusService.createInitialFocus(this.componentRef.location.nativeElement);
        } else {
            // If this screen was updated, focus the previously focused element
            this.focusService.restoreFocus(original);
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
