import { Injectable, Type, ComponentFactoryResolver, ComponentFactory } from '@angular/core';
import {MessageProvider} from '../../shared/providers/message.provider';
import {MessageType} from '../../shared/screen-parts/banner/banner.interface';
import {MessageTypes} from '../messages/message-types';
import { SessionService } from './session.service';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { DialogContentComponent } from '../components/dialog-content/dialog-content.component';
import { MatDialogRef, MatDialog } from '@angular/material';
import { OpenPOSDialogConfig } from '../interfaces/open-pos-dialog-config.interface';
import { BehaviorSubject } from 'rxjs';
import { LifeCycleMessage } from '../messages/life-cycle-message';
import { LifeCycleEvents } from '../messages/life-cycle-events.enum';

@Injectable({
    providedIn: 'root',
})
export class DialogService {

    static dialogs = new Map<string, Type<IScreen>>();

    public dialogRef: MatDialogRef<DialogContentComponent>;

    private closingDialogRef: MatDialogRef<DialogContentComponent>;

    private dialogOpening: boolean;

    private lastDialogType: string;

    private lastDialogId: string;

    constructor(
        private messageProvider: MessageProvider,
        private componentFactoryResolver: ComponentFactoryResolver,
        private session: SessionService,
        private dialog: MatDialog) {
    }

    public start() {
        // Defer set up of dialog subscriptions for updating dialogs until service has been started.
        // Addresses problem of dialogs from server side being shown before app startup has finished.
        // We use a Startup Task to invoke this start method at nearly the end of startup.

        // Pipe all the messages for dialog updates
        this.messageProvider.setMessageType(MessageTypes.DIALOG);
        this.messageProvider.getScopedMessages$().subscribe(m => this.updateDialog(m));

    }

    public addDialog(name: string, type: Type<IScreen>): void {
        if (type === null) {
            throw new Error(`Cannot add null component for dialog with name '${name}'`);
        }

        if (DialogService.dialogs.get(name)) {
            // tslint:disable-next-line:max-line-length
            console.info(`replacing registration of dialog for the key of ${name} in the dialog service`);
            DialogService.dialogs.delete(name);
        }
        DialogService.dialogs.set(name, type);
    }

    public isDialogOpenOrOpening(): boolean {
        if (!this.dialogOpening) {
            return this.isDialogOpen();
        } else {
            return this.dialogOpening;
        }
    }

    public hasDialog(name: string): boolean {
        return DialogService.dialogs.has(name);
    }

    private resolveDialog(type: string): ComponentFactory<IScreen> {
        const dialogType: Type<IScreen> = DialogService.dialogs.get(type);
        if (dialogType) {
            return this.componentFactoryResolver.resolveComponentFactory(dialogType);
        } else {
            console.error(`Could not find a dialog type of: ${type}.  Please register it with the dialog service`);
            return this.componentFactoryResolver.resolveComponentFactory(DialogService.dialogs.get('Blank'));
        }
    }

    // Make this async so we can await it
    public async closeDialog() {
        if (this.dialogRef) {
            console.info('[DialogService] closing dialog ref');
            this.closingDialogRef = this.dialogRef;
            this.dialogRef = null;
            this.closingDialogRef.close();

            this.session.sendMessage( new LifeCycleMessage(LifeCycleEvents.DialogClosing));

            // Wait for the dialog to fully close before moving on
            await this.closingDialogRef.afterClosed().toPromise();

            this.closingDialogRef = null;
        } else if (this.closingDialogRef != null) {
            await this.closingDialogRef.afterClosed().toPromise();
        }
    }

    /** screenType - If given, behavior will only report true if there is a dialog open AND it's of the given type */
    public isDialogOpen(screenType?: string): boolean {
        if (screenType) {
            return screenType === this.lastDialogType && this.dialogRef !== null;
        }

        return this.dialogRef && this.dialogRef !== null;
    }

    private updateDialog(dialog?: any): void {
        if (dialog) {
            const dialogType = this.hasDialog(dialog.subType) ? dialog.subType : 'Dialog';
            if (!this.dialogOpening) {
                console.info('opening dialog \'' + dialogType + '\'');
                this.dialogOpening = true;
                setTimeout(() => this.openDialog(dialog), 0);
            } else {
                console.info(`[DialogService] Not opening dialog! Here's why: dialogOpening? ${this.dialogOpening}`);
            }
        }
    }

    private async openDialog(dialog: any) {
        try {
            const dialogComponentFactory: ComponentFactory<IScreen> = this.resolveDialog(dialog.screenType);
            console.info(`[DialogService] Opening a dialog with a ` +
                `${dialogComponentFactory && dialogComponentFactory.componentType ? dialogComponentFactory.componentType.name : '?'} ` +
                `component as its content`
            );
            let closeable = false;
            let forceReopen = false;
            if (dialog.dialogProperties) {
                closeable = dialog.dialogProperties.closeable;
                forceReopen = dialog.dialogProperties.forceReopen;
            }
            // By default we want to not allow the user to close by clicking off
            // By default we need the dialog to grab focus so you cannont execute actions on the screen
            // behind by hitting enter
            const dialogProperties: OpenPOSDialogConfig = { disableClose: !closeable, autoFocus: true };
            // const dialogComponent = dialogComponentFactory.componentType;
            if (dialog.dialogProperties) {
                // Merge in any dialog properties provided on the screen
                for (const key in dialog.dialogProperties) {
                    if (dialog.dialogProperties.hasOwnProperty(key)) {
                        dialogProperties[key] = dialog.dialogProperties[key];
                    }
                }
                console.info(`Dialog options: ${JSON.stringify(dialogProperties)}`);
            }

            if (!this.dialogRef || !this.dialogRef.componentInstance
                || dialog.screenType !== this.lastDialogType || dialog.screenType === 'Dialog'
                || dialog.refreshAlways || dialog.id !== this.lastDialogId) {

                // We need to make sure to block here before creating the new dialog to make sure the old one
                // is fully closed.
                await this.closeDialog();

                if (!this.dialogRef || !this.dialogRef.componentInstance) {
                    console.info('[DialogService] Dialog \'' + dialog.screenType + '\' opening...');
                    this.session.sendMessage( new LifeCycleMessage(LifeCycleEvents.DialogOpening));
                    this.dialogRef = this.dialog.open(DialogContentComponent, dialogProperties);
                } else {
                    // I don't think this code will ever run
                    console.info('[DialogService] Dialog \'' + dialog.screenType + '\' refreshing content...');
                    this.dialogRef.updateSize('' + dialogProperties.minWidth, '' + dialogProperties.minHeight);
                    this.dialogRef.disableClose = dialogProperties.disableClose;
                }
                this.dialogRef.componentInstance.installScreen(dialogComponentFactory);
                this.session.cancelLoading();
            } else {
                console.info(`Using previously created dialogRef. current dialog type: ${dialog.screenType},
            last dialog type: ${this.lastDialogType}`);
                this.session.cancelLoading();
            }

            console.info('[DialogService] Dialog \'' + dialog.screenType + '\' showing...');
            this.dialogRef.componentInstance.show(dialog);
            console.info('[DialogService] Dialog \'' + dialog.screenType + '\' opened/shown');

            this.lastDialogType = dialog.screenType;
            this.lastDialogId = dialog.id;
        } finally {
            this.dialogOpening = false;
        }
    }

}
