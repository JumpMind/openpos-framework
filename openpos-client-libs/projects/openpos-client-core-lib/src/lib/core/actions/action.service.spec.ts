import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ActionService } from './action.service';
import { of, BehaviorSubject } from 'rxjs';
import { MessageProvider } from '../../shared/providers/message.provider';
import { ToastMessage } from '../messages/toast-message';
import { IConfirmationDialog } from '../interfaces/confirmation-dialog.interface';
import { IActionItem } from '../interfaces/action-item.interface';
import { ElectronService } from 'ngx-electron';

const confirmationDialog: IConfirmationDialog = {
    title: 'Are you sure',
    message: 'you want to do this',
    confirmAction: {action: 'yes'},
    cancelAction: {action: 'no'},
    confirmButtonName: '',
    cancelButtonName: ''
};

const testScreen = {};
const scopedMessages$ = new BehaviorSubject(testScreen);
const allMessages$ = new BehaviorSubject(testScreen);


describe('ActionService', () => {

    let messageProvider: jasmine.SpyObj<MessageProvider>;
    let actionService: ActionService;
    let matDialogRef: jasmine.SpyObj<MatDialogRef<any>>;
    let matDialog: jasmine.SpyObj<MatDialog>;

    function setup() {

        const messageProviderSpy = jasmine.createSpyObj('MessageProvider', ['sendMessage', 'getScopedMessages$', 'getAllMessages$']);
        const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
        const matDialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['afterClosed', 'componentInstance']);
        matDialogRef = matDialogRefSpy;

        TestBed.configureTestingModule({
            providers: [
                ActionService,
                { provide: MessageProvider, useValue: messageProviderSpy},
                { provide: MatDialog, useValue: matDialogSpy },
                ElectronService
            ]
        });

        messageProviderSpy.getScopedMessages$.and.returnValue(scopedMessages$);
        messageProviderSpy.getAllMessages$.and.returnValue(allMessages$);
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

        // WORKING
        it('Should queue action and send when unblocked', fakeAsync(() => {
            setup();

            actionService.doAction({ action: 'Test1', enabled: true });

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test1'}));


            actionService.doAction({ action: 'Test2', queueIfBlocked: true });

            tick();

            expect(messageProvider.sendMessage).not.toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test2'}));

            scopedMessages$.next({willUnblock: true});

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test2'}));

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

        it('Should block subsequent actions until response is received', fakeAsync(() => {
            const action1: IActionItem = { action: 'Test1', enabled: true};
            const action2: IActionItem = { action: 'Test2', enabled: true};

            setup();
            actionService.doAction(action1);

            tick();
            actionService.doAction(action2);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test1'}));
            expect(messageProvider.sendMessage).not.toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test2'}));
        }));

        it('Should unblock actions when a scoped response is received', fakeAsync(() => {
            const action1: IActionItem = { action: 'Test1', enabled: true};
            const action2: IActionItem = { action: 'Test2', enabled: true};

            setup();
            actionService.doAction(action1);

            tick();

            scopedMessages$.next({willUnblock:true});

            tick();

            actionService.doAction(action2);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test1'}));
            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test2'}));
        }));

        it('Should unblock actions when a toast message is recieved', fakeAsync(() => {
            const action1: IActionItem = { action: 'Test1', enabled: true};
            const action2: IActionItem = { action: 'Test2', enabled: true};

            setup();
            actionService.doAction(action1);

            tick();

            const toast = new ToastMessage();
            toast.willUnblock = true;
            allMessages$.next(toast);

            tick();

            actionService.doAction(action2);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test1'}));
            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test2'}));
        }));

        it('Should not block subsequent actions if doNotBlockForResponse is true', fakeAsync(() => {
            const action1: IActionItem = { action: 'Test1', enabled: true, doNotBlockForResponse: true};
            const action2: IActionItem = { action: 'Test2', enabled: true};

            setup();
            actionService.doAction(action1);

            tick();

            actionService.doAction(action2);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test1'}));
            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining({actionName: 'Test2'}));
        }));

        it('Should not show loading if doNotBlockForResponse is true', fakeAsync(() => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();
            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).not.toHaveBeenCalledWith(jasmine.objectContaining({type: 'Loading'}));
        }));

        it('Should send Action payloads with cooresponding actions', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();

            actionService.registerActionPayload('Test', () => 'Test Payload');
            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining(
                { actionName: 'Test', payload: 'Test Payload'}));
        }));

        it('Should use the provided payload over a registered one', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();

            actionService.registerActionPayload('Test', () => 'Test Payload');
            actionService.doAction(action, 'UseThisOne');

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining(
                { actionName: 'Test', payload: 'UseThisOne'}));
        }));

        it('Should remove payload when unregistered', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();

            actionService.registerActionPayload('Test', () => 'Test Payload');
            actionService.unregisterActionPayload('Test');
            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining(
                { actionName: 'Test', payload: undefined}));
        }));

        it('Should remove payload when all unregistered', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();

            actionService.registerActionPayload('Test', () => 'Test Payload');
            actionService.unregisterActionPayloads();
            actionService.doAction(action);

            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalledWith(jasmine.objectContaining(
                { actionName: 'Test', payload: undefined}));
        }));

        it('Should throw and error if payload function errors', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();

            actionService.registerActionPayload('Test', () => { throw new Error(); });

            expect( () => {
                actionService.doAction(action);
                tick();
            }).toThrowError();

        }));

        it('Should not send actions if they are disabled by an action disabler', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();
            actionService.registerActionDisabler('Test', of(true));
            actionService.doAction(action);
            tick();

            expect(messageProvider.sendMessage).not.toHaveBeenCalled();
        }));

        it('Should send actions if they are not disabled', fakeAsync( () => {
            const action: IActionItem = { action: 'Test', enabled: true, doNotBlockForResponse: true };

            setup();
            actionService.registerActionDisabler('Test', of(false));
            actionService.doAction(action);
            tick();

            expect(messageProvider.sendMessage).toHaveBeenCalled();
        }));

    });

    describe('actionIsDisabled', () => {

        it('Should be true if action is disabled by disabler', fakeAsync( () => {
            let result: boolean;
            setup();
            actionService.registerActionDisabler('Test', of(true));
            actionService.actionIsDisabled$('Test').subscribe( r => result = r);
            tick();

            expect(result).toBeTruthy();
        }));

        it('Should handle multiple disablers for a single action', fakeAsync( () => {
            let result: boolean;
            setup();
            actionService.registerActionDisabler('Test', of(true));
            tick();
            actionService.registerActionDisabler('Test', of(false));

            actionService.actionIsDisabled$('Test').subscribe( r => result = r);
            tick();

            expect(result).toBeFalsy();
        }));
    });

});