import { TestBed, tick, fakeAsync } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ServerLogger } from './server-logger.service';
import { DiscoveryService } from '../discovery/discovery.service';
import { of, Subject } from 'rxjs';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';
import { ServerLogEntry } from './server-log-entry';
import { ConfigurationService } from '../services/configuration.service';
import { ServerLoggerConfiguration } from './server-logger-configuration';
import { ConsoleIntercepter, LOGGERS } from './console-interceptor.service';
import { cold } from 'jasmine-marbles';
import { ConsoleInterceptorConfig } from './console-interceptor-config';

describe('ServerLoggerServiceIT', () => {
    let serverLogger: ServerLogger;
    let consoleInterceptor: ConsoleIntercepter;
    let httpTestingController: HttpTestingController;
    let discoveryService: jasmine.SpyObj<DiscoveryService>;
    let configurationService: jasmine.SpyObj<ConfigurationService>;
    let consoleInterceptorBypassService: jasmine.SpyObj<ConsoleInterceptorBypassService>;
    let serverLoggerConfigSubject: Subject<ServerLoggerConfiguration>;
    let consoleInterceptorConfigSubject: Subject<ConsoleInterceptorConfig>;
    const config = new ConsoleInterceptorConfig();

    const bypassMessages = [];

    const discoverySpy = jasmine.createSpyObj('DiscoveryService', ['getDeviceAppApiServerBaseUrl$']);
    const configurationSpy = jasmine.createSpyObj('ConfigurationService', ['getConfiguration']);
    const consoleInterceptorBypassServiceSpy =
        jasmine.createSpyObj('ConsoleInterceptorBypassService', ['log', 'error', 'info', 'warn', 'debug', 'getMessages$']);

    function getBypassMessages() {
        let marbles = '-';
        const values = {};

        bypassMessages.forEach( ( m, index ) => {
            marbles = marbles.concat(String.fromCharCode(index + 97) + '-');
            values[String.fromCharCode(index + 97)] = m;
        });
        return cold(marbles, values);
    }

    function setup() {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            providers: [
                ServerLogger,
                ConsoleIntercepter,
                { provide: DiscoveryService, useValue: discoverySpy },
                { provide: ConfigurationService, useValue: configurationSpy },
                { provide: ConsoleInterceptorBypassService, useValue: consoleInterceptorBypassServiceSpy},
                { provide: LOGGERS, useExisting: ServerLogger, multi: true, deps: [DiscoveryService, ConsoleIntercepter] },
            ]
        });
        httpTestingController = TestBed.inject(HttpTestingController);
        discoveryService = TestBed.inject(DiscoveryService) as jasmine.SpyObj<DiscoveryService>;
        configurationService = TestBed.inject(ConfigurationService) as jasmine.SpyObj<ConfigurationService>;
        consoleInterceptorBypassService = TestBed.inject(ConsoleInterceptorBypassService) as jasmine.SpyObj<ConsoleInterceptorBypassService>;
        consoleInterceptorBypassService.getMessages$.and.callFake(getBypassMessages);
        discoveryService.getDeviceAppApiServerBaseUrl$.and.returnValue(of('/test/api'));
        serverLoggerConfigSubject = new Subject<ServerLoggerConfiguration>();
        consoleInterceptorConfigSubject = new Subject<ConsoleInterceptorConfig>();
        configurationService.getConfiguration.and.callFake((type: string) => {
            if (type === 'server-logger') {
                return serverLoggerConfigSubject;
            } else if (type === 'console-interceptor') {
                return consoleInterceptorConfigSubject;
            }
        });
        serverLogger = TestBed.inject(ServerLogger);
        consoleInterceptor = TestBed.inject(ConsoleIntercepter);

    }

    it('Should handle logging object containing a circular reference to the ServerLogger', fakeAsync(() => {
        setup();
        // Set shipToServer time to 200ms for period of sending log
        // messages to server
        serverLoggerConfigSubject.next(new ServerLoggerConfiguration(200));

        // Enable console message interception
        const consoleConfig = new ConsoleInterceptorConfig();
        consoleConfig.enable = true;
        consoleInterceptorConfigSubject.next(consoleConfig);

        // Set up object with circular reference
        const parentObj = {name: 'p', children: []};
        const childObj = { name: 'c', parent: parentObj};
        parentObj.children.push(childObj);

        console.log(parentObj);

        tick(300);

        // Ensure object with circular reference successfully posted
        const req = httpTestingController.expectOne('/test/api/clientlogs');
        expect(req.request.method).toBe('POST');
        const body = req.request.body as ServerLogEntry[];

        expect(body.length).toBe(1);
        expect(body[0].message).toBe('{"name":"p","children":[{"name":"c"}]}');
        serverLogger.ngOnDestroy();
    }));

});
