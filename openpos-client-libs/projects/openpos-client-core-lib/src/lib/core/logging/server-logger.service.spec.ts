import { TestBed, tick, fakeAsync } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ServerLogger } from './server-logger.service';
import { DiscoveryService } from '../discovery/discovery.service';
import { of, Subject } from 'rxjs';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';
import { ServerLogEntry } from './server-log-entry';
import { ConfigurationService } from '../services/configuration.service';
import { ServerLoggerConfiguration } from './server-logger-configuration';

describe('ServerLoggerService', () => {
    let serverLogger: ServerLogger;
    let httpTestingController: HttpTestingController;
    let discoveryService: jasmine.SpyObj<DiscoveryService>;
    let configurationService: jasmine.SpyObj<ConfigurationService>;
    let consoleInterceptorBypassService: jasmine.SpyObj<ConsoleInterceptorBypassService>;
    let configSubject: Subject<ServerLoggerConfiguration>;
    const discoverySpy = jasmine.createSpyObj('DiscoveryService', ['getDeviceAppApiServerBaseUrl$']);
    const configurationSpy = jasmine.createSpyObj('ConfigurationService', ['getConfiguration']);
    const consoleInterceptorBypassServiceSpy =
    jasmine.createSpyObj('ConsoleInterceptorBypassService', ['log', 'error', 'info', 'warn', 'debug']);

    function setup() {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            providers: [
                ServerLogger,
                { provide: DiscoveryService, useValue: discoverySpy },
                { provide: ConfigurationService, useValue: configurationSpy },
                { provide: ConsoleInterceptorBypassService, useValue: consoleInterceptorBypassServiceSpy}
            ]
        });
        httpTestingController = TestBed.get(HttpTestingController);
        discoveryService = TestBed.get(DiscoveryService);
        configurationService = TestBed.get(ConfigurationService);
        consoleInterceptorBypassService = TestBed.get(ConsoleInterceptorBypassService);
        discoveryService.getDeviceAppApiServerBaseUrl$.and.returnValue(of('/test/api'));
        configSubject = new Subject<ServerLoggerConfiguration>();
        configurationService.getConfiguration.and.returnValue(configSubject);
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

    it('Should change the Buffer time when updated through a config message', fakeAsync(() => {
        setup();

        serverLogger.log('Test log message');
        serverLogger.info('Test info message');

        tick(300);

        httpTestingController.expectOne('/test/api/clientlogs');

        configSubject.next(new ServerLoggerConfiguration(500));

        serverLogger.log('Test log message');
        serverLogger.info('Test info message');

        tick(300);

        httpTestingController.expectNone('/test/api/clientlogs');
        httpTestingController.verify();
        serverLogger.ngOnDestroy();
    }));

    it('Should not lose any log messages when updating configuration', fakeAsync(() => {
        setup();

        serverLogger.log('Test log message');
        serverLogger.info('Test info message');

        tick(200);

        httpTestingController.expectNone('/test/api/clientlogs');

        configSubject.next(new ServerLoggerConfiguration(500));

        serverLogger.log('Test log message 2');
        serverLogger.info('Test info message 2');

        tick(500);

        const requests = httpTestingController.match('/test/api/clientlogs');

        expect(requests.length).toBe(2);
        expect(requests[0].request.body.length).toBe(2);
        expect(requests[1].request.body.length).toBe(2);

        requests.forEach(r => r.flush('', { status: 200, statusText: 'Ok' }));

        httpTestingController.verify();
        serverLogger.ngOnDestroy();

    }));
});
