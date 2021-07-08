import {ComponentFactory, ComponentFactoryResolver, Injectable, Type} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import { filter, tap } from 'rxjs/operators';
import {IScreen} from '../../shared/components/dynamic-screen/screen.interface';
import {MessageProvider} from '../../shared/providers/message.provider';
import {DialogContentComponent} from '../components/dialog-content/dialog-content.component';
import {OpenPOSDialogConfig} from '../interfaces/open-pos-dialog-config.interface';
import {LifeCycleEvents} from '../messages/life-cycle-events.enum';
import {LifeCycleMessage} from '../messages/life-cycle-message';
import {MessageTypes} from '../messages/message-types';
import {SessionService} from './session.service';
import { Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class DialogService {

    static dialogs = new Map<string, Type<IScreen>>();

    public dialogRef: MatDialogRef<DialogContentComponent>;
    public beforeOpened$ = new Subject<OpenPOSDialogConfig>();
    public afterOpened$ = new Subject<MatDialogRef<DialogContentComponent>>();
    public beforeClosed$ = new Subject<MatDialogRef<DialogContentComponent>>();
    public afterClosed$ = new Subject<MatDialogRef<DialogContentComponent>>();

    private closingDialogRef: MatDialogRef<DialogContentComponent>;
    private dialogOpening: boolean;
    private lastDialogType: string;
    private lastDialogId: string;
    private lastScreenSeq: number;

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
        this.session.getMessages(MessageTypes.DIALOG).subscribe(m => this.updateDialog(m));
        this.session.getMessages(MessageTypes.SCREEN).pipe(tap(x => console.log("Last screen sequence = ", x.sequenceNumber))).subscribe( m => this.lastScreenSeq = m.sequenceNumber)
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

    public isDialogOpening(): boolean {
        return this.dialogOpening;
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

            this.session.sendMessage( new LifeCycleMessage(LifeCycleEvents.DialogClosing, null));

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
        if(dialog){
            const dialogType = this.hasDialog(dialog.subType) ? dialog.subType : 'Dialog';
            if (!this.dialogOpening) {
                console.info('opening dialog \'' + dialogType + '\'');
                setTimeout(() => this.openDialog(dialog), 0);
            } else {
                console.info(`[DialogService] putting off the opening of the dialog to the future because another dialog is currently opening`);
                setTimeout(() => this.updateDialog(dialog), 100);
            }
        }
    }

    private async openDialog(dialog: any) {
        if (!dialog || dialog.sequenceNumber < this.lastScreenSeq) {
            console.log(`Not opening dialog because the sequence is older than the last screen`);
            return;
        }
        try {
            this.dialogOpening = true;
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
            const dialogProperties: OpenPOSDialogConfig = {disableClose: !closeable, autoFocus: true};
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
                    this.session.sendMessage(new LifeCycleMessage(LifeCycleEvents.DialogOpening, dialog));
                    this.beforeOpened$.next(dialogProperties);
                    this.dialogRef = this.dialog.open(DialogContentComponent, dialogProperties);

                    const dialogRef = this.dialogRef;
                    this.dialogRef.beforeClosed().subscribe(() => this.beforeClosed$.next(dialogRef));
                    this.dialogRef.afterClosed().subscribe(() => this.afterClosed$.next(dialogRef));
                    this.dialogRef.afterOpened().subscribe(() => this.afterOpened$.next(dialogRef));
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
        console.log("screen updated");
        this.session.sendMessage(new LifeCycleMessage(LifeCycleEvents.ScreenUpdated, dialog));
    }

}
