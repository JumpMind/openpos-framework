import { SessionService } from './session.service';
import { Logger } from './logger.service';
import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material';
import { IActionItem } from '../interfaces/action-item.interface';
import { IConfirmationDialog } from '../interfaces/confirmation-dialog.interface';
import { ActionService } from './action.service';
import { of } from 'rxjs';
import { MessageProvider } from '../../shared/providers/message.provider';
import { cold } from 'jasmine-marbles';


const confirmationDialog: IConfirmationDialog = {
    title: 'Are you sure',
    message: 'you want to do this',
    confirmAction: {action: 'yes'},
    cancelAction: {action: 'no'}
};

let testScreen = {};


describe('ActionService', () => {

    let messageProvider: jasmine.SpyObj<MessageProvider>;
    let loggerService: jasmine.SpyObj<Logger>;
    let actionService: ActionService;
    let matDialogRef: jasmine.SpyObj<MatDialogRef<any>>;
    let matDialog: jasmine.SpyObj<MatDialog>;

    function setup(){

        const messageProviderSpy = jasmine.createSpyObj('MessageProvider', ['sendMessage', 'getScopedMessages$']);
        const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
        const matDialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['afterClosed', 'componentInstance']);
        const loggerSpy = jasmine.createSpyObj('Logger', ['info']);
        matDialogRef = matDialogRefSpy;

        TestBed.configureTestingModule({
            providers: [
                ActionService,
                { provide: MessageProvider, useValue: messageProviderSpy},
                { provide: MatDialog, useValue: matDialogSpy },
                { provide: Logger, useValue: loggerSpy },
            ]
        });

        messageProviderSpy.getScopedMessages$.and.returnValue(cold('---x|', {x: testScreen}));
        loggerService = TestBed.get(Logger);
        messageProvider = TestBed.get(MessageProvider);
        actionService = TestBed.get(ActionService);
        matDialog = TestBed.get(MatDialog);
        matDialog.open.and.returnValue(matDialogRefSpy);
    }

    describe( 'doAction', () => {
        it('Should not send message if the action is not enabled', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: false };

            setup();

            actionService.doAction(action);

            tick();
            expect(messageProvider.sendMessage).not.toHaveBeenCalled();
        }));

        it('Should show confirmation dialog if confirmation dialog property is set', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: true, confirmationDialog };

            setup();
            // return false so that we don't confirm
            matDialogRef.afterClosed.and.returnValue(of(false));
            actionService.doAction(action);

            tick();

            expect(matDialog.open).toHaveBeenCalled();
        }));

        it('Should not send the action if confirmation is required and answered no', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: true, confirmationDialog };

            setup();
            // return false so that we don't confirm
            matDialogRef.afterClosed.and.returnValue(of(false));

            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).not.toHaveBeenCalled();
        }));

        it('Should send the action if action is enabled and there is no confirmation', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: true };

            setup();
            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalled();
        }));

        it('Should send the action if action is enabled and there is confirmation and answered yes', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: true, confirmationDialog };

            setup();
            // return false so that we don't confirm
            matDialogRef.afterClosed.and.returnValue(of(true));

            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalled();
        }));

        it('Should show loading after an action is sent', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: true };

            setup();
            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({type: 'Loading'}));
        }));

        it('Should block subsequent actions until response is received', () => {

        });

        it('Should not block subsequent actions if doNotBlockForResponse is true', () => {

        });

        it('Should not show loading if doNotBlockForResponse is true', () => {

        });
    });

});

