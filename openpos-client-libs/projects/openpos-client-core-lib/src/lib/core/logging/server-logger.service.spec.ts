import { TestBed, tick, fakeAsync } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ServerLogger } from './server-logger.service';
import { PersonalizationService } from '../personalization/personalization.service';
import { of } from 'rxjs';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';
import { ServerLogEntry } from './server-log-entry';

describe('ServerLoggerService', () => {
    let serverLogger: ServerLogger;
    let httpTestingController: HttpTestingController;
    let personalizationService: jasmine.SpyObj<PersonalizationService>;
    let consoleInterceptorBypassService: jasmine.SpyObj<ConsoleInterceptorBypassService>;

    const personalizationSpy = jasmine.createSpyObj('PersonalizationService', ['getDeviceAppApiServerBaseUrl$']);
    const consoleInterceptorBypassServiceSpy =
    jasmine.createSpyObj('ConsoleInterceptorBypassService', ['log', 'error', 'info', 'warn', 'debug']);

    function setup() {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            providers: [
                ServerLogger,
                { provide: PersonalizationService, useValue: personalizationSpy },
                { provide: ConsoleInterceptorBypassService, useValue: consoleInterceptorBypassServiceSpy}
            ]
        });
        httpTestingController = TestBed.get(HttpTestingController);
        personalizationService = TestBed.get(PersonalizationService);
        consoleInterceptorBypassService = TestBed.get(ConsoleInterceptorBypassService);
        personalizationService.getDeviceAppApiServerBaseUrl$.and.returnValue(of('/test/api'));
        serverLogger = TestBed.get(ServerLogger);
    }

    it('Should collect logs and send them in a batch to the server', fakeAsync(() => {
        setup();

        serverLogger.log('Test log message');
        serverLogger.info('Test info message');
        serverLogger.error('Test error message');
        serverLogger.warn('Test warn message');
        serverLogger.debug('Test debug message');

        tick(300);

        const req = httpTestingController.expectOne('/test/api/clientlogs');
        expect(req.request.method).toBe('POST');
        const body = req.request.body as ServerLogEntry[];

        expect(body.length).toBe(5);

        req.flush('', { status: 200, statusText: 'Ok' });

        httpTestingController.verify();
        serverLogger.ngOnDestroy();
    }));

    it('Should output logs to the InterceptorBypass if the POST fails', fakeAsync(() => {
        setup();

        serverLogger.log('Test log message');
        serverLogger.info('Test info message');
        serverLogger.error('Test error message');
        serverLogger.warn('Test warn message');
        serverLogger.debug('Test debug message');

        tick(300);

        const req = httpTestingController.expectOne('/test/api/clientlogs');
        expect(req.request.method).toBe('POST');

        req.flush('', { status: 404, statusText: 'Error' });

        expect(consoleInterceptorBypassService.log).toHaveBeenCalledWith('Test log message');
        expect(consoleInterceptorBypassService.info).toHaveBeenCalledWith('Test info message');
        expect(consoleInterceptorBypassService.error).toHaveBeenCalledWith('Test error message');
        expect(consoleInterceptorBypassService.warn).toHaveBeenCalledWith('Test warn message');
        expect(consoleInterceptorBypassService.debug).toHaveBeenCalledWith('Test debug message');

        httpTestingController.verify();
        serverLogger.ngOnDestroy();
    }));
});
