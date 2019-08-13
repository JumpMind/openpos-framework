import { TestBed } from '@angular/core/testing';
import { LogIntercepter, LOGGERS, LOG_APPENDERS } from './console-interceptor.service';
import { ILogger } from './logger.interface';
import { SessionService } from '../services/session.service';
import { cold, getTestScheduler } from 'jasmine-marbles';
import { ConsoleInterceptorConfig } from './console-interceptor-config';
import { ILogAppender } from './log-appender.interface';
import { LogMethodType } from './log-message-type.enum';

describe( 'ConsoleInterceptor', () => {
    let loggers: ILogger[];
    let appenders: ILogAppender[];
    const interceptMethods = ['log', 'error', 'info', 'warn', 'debug'];
    let sessionService: jasmine.SpyObj<SessionService>;
    const config = new ConsoleInterceptorConfig();
    let appenderFunc: ( message: string, messageType: LogMethodType) => string;

    function getConfig() {
        return cold('-x', {x: config});
    }

    function appender( message: string, messageType: LogMethodType ) {
        if ( appenderFunc ) {
            return appenderFunc( message, messageType);
        }
        return message;
    }

    beforeEach( () => {
        appenderFunc = null;
        const logSpy = jasmine.createSpyObj('TestLogger', interceptMethods);
        const appenderSpy = jasmine.createSpyObj('TestAppender', ['append']);
        const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages']);
        const consoleSpy = jasmine.createSpyObj('console', interceptMethods);
        console = consoleSpy;

        TestBed.configureTestingModule({
            providers: [
                LogIntercepter,
                {provide: SessionService, useValue: sessionSpy },
                {provide: LOGGERS, useValue: logSpy, multi: true},
                {provide: LOG_APPENDERS, useValue: appenderSpy, multi: true}
            ]
        });
        sessionService = TestBed.get(SessionService);
        sessionService.getMessages.and.callFake(getConfig);
        TestBed.get(LogIntercepter);
        loggers = TestBed.get(LOGGERS);
        appenderSpy.append.and.callFake(appender);
        appenders = TestBed.get(LOG_APPENDERS);
    });

    describe( 'Intercepting enabled', () => {
        beforeEach(() => {
            config.enable = true;
        });

        ['log', 'error', 'info', 'warn', 'debug'].forEach( method => {
            it(`Should intercept console.${method} and send to registered loggers`, () => {

                getTestScheduler().flush();

                console[method](`Test ${method} message`);

                loggers.forEach( l => {
                    expect(l[method]).toHaveBeenCalledWith(`Test ${method} message`);
                });
            });

            it(`Should call all appenders`, () => {

                appenderFunc = ( message, type) => {
                    return message + ' appended';
                };

                getTestScheduler().flush();

                console[method](`Test ${method} message`);

                appenders.forEach( a => {
                    expect(a.append).toHaveBeenCalled();
                });

                loggers.forEach( l => {
                    expect(l[method]).toHaveBeenCalledWith(`Test ${method} message appended`);
                });
            });
        });
    } );

    describe( 'Intercepting disabled', () => {
        beforeEach(() => {
            config.enable = false;
        });

        ['log', 'error', 'info', 'warn', 'debug'].forEach( method => {
            it(`Should not intercept console.${method}`, () => {

                getTestScheduler().flush();

                console[method](`Test ${method} message`);

                loggers.forEach( l => {
                    expect(l[method]).not.toHaveBeenCalled();
                    expect(console[method]).toHaveBeenCalledWith(`Test ${method} message`);
                });
            });
        });
    });



});
