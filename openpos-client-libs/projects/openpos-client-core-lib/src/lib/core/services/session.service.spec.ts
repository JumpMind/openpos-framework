import { ElectronService } from 'ngx-electron';
import { Logger } from './logger.service';
import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { PersonalizationService } from './personalization.service';
import { MatDialog } from '@angular/material';
import { StompRService } from '@stomp/ng2-stompjs';
import { DeviceService } from './device.service';
import { AppInjector } from '../app-injector';
import { Injector } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';


describe('SessionService', () => {

    let stompServiceSpy: jasmine.SpyObj<StompRService>;
    let sessionService: SessionService;
    let deviceServiceSpy: jasmine.SpyObj<DeviceService>;
    let loggerServiceSpy: jasmine.SpyObj<Logger>;

    beforeEach(() => {
        const stompSpy = jasmine.createSpyObj('StompRService', ['publish']);
        const personalizationSpy = jasmine.createSpyObj('PersonalizationService', ['getDeviceId']);
        const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
        const deviceSpy = jasmine.createSpyObj('DeviceService', ['isRunningInCordova']);
        const loggerSpy = jasmine.createSpyObj('Logger', ['info']);
        const electronSpy = jasmine.createSpyObj('ElectronService', ['isElectronApp']);
        electronSpy.isElectronApp = false;

        TestBed.configureTestingModule({
            imports: [
                HttpClientModule
              ],
            providers: [
                { provide: PersonalizationService, useValue: personalizationSpy },
                SessionService,
                { provide: MatDialog, useValue: matDialogSpy },
                { provide: StompRService, useValue: stompSpy },
                { provide: Logger, useValue: loggerSpy },
                { provide: DeviceService, useValue: deviceSpy},
                { provide: ElectronService, useValue: electronSpy},
            ]
        });

        AppInjector.Instance = TestBed.get(Injector);
        deviceServiceSpy = TestBed.get(DeviceService);
        deviceServiceSpy.isRunningInCordova.and.returnValue(false);

        loggerServiceSpy = TestBed.get(Logger);

        stompServiceSpy = TestBed.get(StompRService);
        sessionService = TestBed.get(SessionService);
    });

    describe('onAction', () => {
        it('should call StompService.publish when called', () => {
            sessionService.onAction('foo', 'bar');
            expect(stompServiceSpy.publish.calls.count).toBeTruthy();
        });
    });

});
