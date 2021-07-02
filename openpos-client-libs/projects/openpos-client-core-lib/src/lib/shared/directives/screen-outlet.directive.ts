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
import {PersonalizationService} from '../../core/personalization/personalization.service';
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
import { filter } from 'rxjs/operators';

// tslint:disable-next-line:directive-selector
@Directive({ selector: '[openposScreenOutlet]' })
export class OpenposScreenOutletDirective implements OnInit, OnDestroy {

    private componentRef: ComponentRef<any> | null = null;
    @Output() componentEmitter = new EventEmitter<{ componentRef: ComponentRef<any>, screen: any }>();
    @Input() unsubscribe = true;

    public screenTypeName: string;
    private screenName: string;
    private screenId: string;

    //public classes = '';

    public currentTheme: string;

    private themeClazzes = [];
    private personalizationClazzes = [];

    private subscriptions = new Subscription();

    constructor(
        private screenService: ScreenService,
        private viewContainerRef: ViewContainerRef,
        private session: SessionService,
        private configurationService: ConfigurationService,
        private overlayContainer: OverlayContainer,
        private dialogService: DialogService,
        private focusService: FocusService,
        private screenCreator: ScreenCreatorService,
        private personalization: PersonalizationService,
        private renderer: Renderer2) {
    }

    ngOnInit(): void {
        this.subscriptions.add(this.session.screenMessage$.subscribe((message) => this.handle(message)));
        this.subscriptions.add(this.session.getMessages(MessageTypes.LIFE_CYCLE_EVENT).subscribe(message => this.handleLifeCycleEvent(message)));
        this.subscriptions.add(this.configurationService.theme$.subscribe( theme => {
            this.updateTheme(theme);
        }));

        this.subscriptions.add(this.personalization.getPersonalizationProperties$().pipe(
            filter(p => !!p)
        ).subscribe(properties => {
            this.updatePersonalizationProperties(properties);
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

    protected async updateScreen(screen: any) {
        console.log('Handling screen: ', screen);
        if ( this.dialogService.isDialogOpen() ) {
            console.log('Try closing open dialog')
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

            trap = true;
        }

        if (this.componentRef.instance.show) {
            this.componentRef.instance.show(screen);
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

        this.updateClazzes( this.personalizationClazzes, this.personalizationClazzes);
        this.updateClazzes( this.themeClazzes, this.themeClazzes);

        // Output the componentRef and screen to the training-wrapper
        this.componentEmitter.emit({ componentRef: this.componentRef, screen });
        this.session.sendMessage( new LifeCycleMessage(LifeCycleEvents.ScreenUpdated, screen));

    }

    protected updateTheme(theme: string) {
        console.info('updating theme to ' + theme);

        let clazzes = !!theme ? theme.split(' ') : [];
        this.updateClazzes(this.themeClazzes, clazzes);
        this.themeClazzes = clazzes;
    }

    private updatePersonalizationProperties( properties: Map<string, string>){
        let clazzes = [];
        properties.forEach( (value, key) => {
            clazzes.push( `personalization-${key}-${value}`);
        });
        this.updateClazzes(this.personalizationClazzes, clazzes);
        this.personalizationClazzes = clazzes;
    }

    private updateClazzes( oldClazzes: string[], newClazzes: string[]){
        //remove old classes
        oldClazzes.forEach( clazz => {
            this.overlayContainer.getContainerElement().classList.remove(clazz);
            if( this.componentRef ){
                const parent = this.renderer.parentNode(this.componentRef.location.nativeElement);
                this.renderer.removeClass(parent, clazz);
            }
        });

        //add new classes
        newClazzes.forEach( clazz => {
            this.overlayContainer.getContainerElement().classList.add(clazz);
            if( this.componentRef ){
                const parent = this.renderer.parentNode(this.componentRef.location.nativeElement);
                this.renderer.addClass(parent, clazz);
            }
        });
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

        console.info(msg);
    }
}
