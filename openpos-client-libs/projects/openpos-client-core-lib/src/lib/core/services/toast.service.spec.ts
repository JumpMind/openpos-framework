import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { ToastService } from './toast.service';
import { of } from 'rxjs';
import { Injector } from '@angular/core';
import {CloseToastMessage, IToastScreen, ToastType} from '../interfaces/toast-screen.interface';
import { AppInjector } from '../app-injector';
import {ToastrService} from 'ngx-toastr';
import {ToastComponent} from "../../shared/components/toast/toast.component";


describe('ToastService', () => {

    let sessionServiceSpy: jasmine.SpyObj<SessionService>;
    let toastrServiceSpy: jasmine.SpyObj<ToastrService>;

    let toastService: ToastService;
    const testToast: IToastScreen = {
        locale: 'en-us',
        name: 'Toast',
        type: 'Toast',
        screenType: 'Toast',
        message: 'Hi',
        duration: 2500,
        verticalPosition: 'top',
        toastType: ToastType.Success
    };

    const persistedToast: IToastScreen = {
        locale: 'en-us',
        name: 'Toast',
        type: 'Toast',
        screenType: 'Toast',
        message: 'Hi',
        duration: 2500,
        verticalPosition: 'top',
        toastType: ToastType.Success,
        persistent: true,
        persistedId: 'persistedId'
    };

    beforeEach(() => {
        const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages', 'cancelLoading']);
        const toastrSpy = jasmine.createSpyObj('ToastrService', ['show', 'clear']);

        TestBed.configureTestingModule({
            providers: [

                { provide: SessionService, useValue: sessionSpy },
                { provide: ToastrService, useValue: toastrSpy },
                ToastService,
            ]
        });

        AppInjector.Instance = TestBed.inject(Injector);
        sessionServiceSpy = TestBed.inject(SessionService) as jasmine.SpyObj<SessionService>;
        sessionServiceSpy.getMessages.and.returnValue(of(testToast));

        toastrServiceSpy = TestBed.inject(ToastrService) as jasmine.SpyObj<ToastrService>;
        toastrServiceSpy.show.and.returnValue({ toastRef: { componentInstance: {}}})

        toastService = TestBed.inject(ToastService);

    });

    describe('constructor', () => {
        it('should call ToastrService.show when called', () => {
            expect(toastrServiceSpy.show.calls.count).toBeTruthy();
            expect(toastrServiceSpy.show).toHaveBeenCalledWith(
                    testToast.message,
                    null,
                    {
                        timeOut: 2500,
                        extendedTimeOut: 2500,
                        disableTimeOut: false,
                        tapToDismiss: true,
                        positionClass: 'toast-top-center',
                        toastClass: 'ngx-toastr app-toast-success',
                        toastComponent: ToastComponent
                    },
                );
        });

        it('should save persisted messages', () => {
            sessionServiceSpy.getMessages.and.returnValue(of(persistedToast));
            expect(toastService.persistedToasts.get('persistedId')).not.toBeNull();
        });

        it('should remove persisted message by ID', () => {
            sessionServiceSpy.getMessages.and.returnValue(of(persistedToast));
            const closeToast: CloseToastMessage = {
                persistedId: 'persistedId'
            };
            sessionServiceSpy.getMessages.and.returnValue(of(closeToast));
            expect(toastService.persistedToasts.get('persistedId')).toBeFalsy();
        });
    });
});
