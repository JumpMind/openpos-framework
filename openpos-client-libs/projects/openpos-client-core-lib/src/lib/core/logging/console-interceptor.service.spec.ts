import { TestBed, fakeAsync, flushMicrotasks } from '@angular/core/testing';
import { ConsoleIntercepter, LOGGERS } from './console-interceptor.service';
import { ILogger } from './logger.interface';
import { SessionService } from '../services/session.service';
import { cold, getTestScheduler } from 'jasmine-marbles';
import { ConsoleInterceptorConfig } from './console-interceptor-config';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';
import { Subject } from 'rxjs';

describe( 'ConsoleInterceptor', () => {
    let loggers: ILogger[];
    const interceptMethods = ['log', 'error', 'info', 'warn', 'debug'];
    let sessionService: jasmine.SpyObj<SessionService>;
    let consoleInterceptorBypassService: jasmine.SpyObj<ConsoleInterceptorBypassService>;
    const config = new ConsoleInterceptorConfig();
    const originalConsoleMethodSpies = new Map<string, any>();
    const bypassMessages = [];

    function addBypassMessage(method: string, message: string) {
        bypassMessages.push({ method, message});
    }

    function getConfig() {
        return cold('-x', {x: config});
    }

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
        const logSpy = jasmine.createSpyObj('TestLogger', interceptMethods);
        const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages']);
        const consoleIntercepterBypassSpy = jasmine.createSpyObj('ConsoleInterceptorBypassService', ['getMessages$']);
        const consoleSpy = jasmine.createSpyObj('console', interceptMethods);
        console = consoleSpy;
        originalConsoleMethodSpies.set('log', console.log);
        originalConsoleMethodSpies.set('error', console.error);
        originalConsoleMethodSpies.set('warn', console.warn);
        originalConsoleMethodSpies.set('info', console.info);
        originalConsoleMethodSpies.set('debug', console.debug);

        TestBed.configureTestingModule({
            providers: [
                ConsoleIntercepter,
                {provide: ConsoleInterceptorBypassService, useValue: consoleIntercepterBypassSpy},
                {provide: SessionService, useValue: sessionSpy },
                {provide: LOGGERS, useValue: logSpy, multi: true},
            ]
        });
        sessionService = TestBed.get(SessionService);
    }

    function setupSync() {
        setup();
        sessionService.getMessages.and.callFake(getConfig);

        consoleInterceptorBypassService = TestBed.get(ConsoleInterceptorBypassService);
        consoleInterceptorBypassService.getMessages$.and.callFake(getBypassMessages);
        TestBed.get(ConsoleIntercepter);
        loggers = TestBed.get(LOGGERS);
    }

    function setupAsync( configSubject: Subject<ConsoleInterceptorConfig> ) {
        setup();

        sessionService.getMessages.and.callFake(() => configSubject);

        consoleInterceptorBypassService = TestBed.get(ConsoleInterceptorBypassService);
        consoleInterceptorBypassService.getMessages$.and.callFake(getBypassMessages);
        TestBed.get(ConsoleIntercepter);
        loggers = TestBed.get(LOGGERS);
    }

    describe( 'Intercepting enabled', () => {
        beforeEach(() => {
            config.enable = true;
        });

        ['log', 'error', 'info', 'warn', 'debug'].forEach( method => {
            it(`Should intercept console.${method} and send to registered loggers`, () => {
                setupSync();

                getTestScheduler().flush();

                console[method](`Test ${method} message`);

                loggers.forEach( l => {
                    expect(l[method]).toHaveBeenCalledWith(`Test ${method} message`);
                });
                expect(originalConsoleMethodSpies.get(method)).not.toHaveBeenCalledWith(`Test ${method} message`);
            });

        });

        it('Using the ConsoleInterceptorBypassService should go straight to the original console methods', () => {
            addBypassMessage('log', 'BypassLogMessage');
            addBypassMessage('error', 'BypassErrorMessage');

            setupSync();

            getTestScheduler().flush();

            expect(originalConsoleMethodSpies.get('log')).toHaveBeenCalledWith(`BypassLogMessage`);
            expect(originalConsoleMethodSpies.get('error')).toHaveBeenCalledWith('BypassErrorMessage');
        });

        ['log', 'error', 'info', 'warn', 'debug'].forEach( method => {
                it(`Should restore the original ${method} function when configuration is changed back to disabled`, fakeAsync(() => {

                    const configSubject = new Subject<ConsoleInterceptorConfig>();
                    setupAsync(configSubject);

                    config.enable = true;

                    configSubject.next(config);

                    flushMicrotasks();

                    config.enable = false;

                    configSubject.next(config);

                    flushMicrotasks();

                    console[method](`Test ${method} message`);
                    expect(originalConsoleMethodSpies.get(method)).toHaveBeenCalledWith(`Test ${method} message`);
                }));
        } );
    });

    describe( 'Intercepting disabled', () => {
        beforeEach(() => {
            config.enable = false;
        });

        ['log', 'error', 'info', 'warn', 'debug'].forEach( method => {
            it(`Should not intercept console.${method}`, () => {

                setupSync();

                getTestScheduler().flush();

                console[method](`Test ${method} message`);

                loggers.forEach( l => {
                    expect(l[method]).not.toHaveBeenCalled();
                });
                expect(console[method]).toHaveBeenCalledWith(`Test ${method} message`);
            });
        });
    });



});
