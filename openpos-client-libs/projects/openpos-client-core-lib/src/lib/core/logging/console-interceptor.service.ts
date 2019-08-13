import { Injectable, InjectionToken, Optional, Inject } from '@angular/core';
import { ILogger } from './logger.interface';
import { SessionService } from '../services/session.service';
import { filter } from 'rxjs/operators';
import { ConsoleInterceptorConfig } from './console-interceptor-config';
import { MessageTypes } from '../messages/message-types';
import { ILogAppender } from './log-appender.interface';
import { LogMethodType } from './log-message-type.enum';

export const LOGGERS = new InjectionToken<ILogger[]>('Loggers');
export const LOG_APPENDERS = new InjectionToken<ILogAppender[]>('LogPrepender');

@Injectable({
    providedIn: 'root'
})
export class LogIntercepter {

    private configuration: ConsoleInterceptorConfig;
    private originalMethods = new Map<string, any>();

    constructor( @Optional() @Inject(LOGGERS) private loggers: Array<ILogger>,
                 @Optional() @Inject(LOG_APPENDERS) private appenders: Array<ILogAppender>,
                 sessionService: SessionService ) {

        sessionService.getMessages(MessageTypes.CONFIG_CHANGED).pipe(
            filter( m => m.configType === 'ConsoleInterceptor')
            ).subscribe( message => {
                this.configuration = message as ConsoleInterceptorConfig;
                if ( !!this.configuration && this.configuration.enable && !!this.loggers) {
                    console.log('intercepting console methods');
                    this.intercept('log');
                    this.intercept('warn');
                    this.intercept('info');
                    this.intercept('debug');
                    this.intercept('error');
                } else {
                    this.restore('log');
                    this.restore('warn');
                    this.restore('info');
                    this.restore('debug');
                    this.restore('error');
                    console.log('restored console methods');
                }
            });
    }

    private intercept(methodName: string) {
        this.originalMethods.set(methodName, console[methodName]);
        console[methodName] = (args) => {

            let message = args;
            if ( !!this.appenders ) {
                this.appenders.forEach( a => {
                    message = a.append(message, LogMethodType[methodName]);
                });
            }
            this.loggers.forEach( logger => {
                logger[methodName](message);
            });
        };
    }

    private restore(methodName: string) {
        if (this.originalMethods.has(methodName)) {
            console[methodName] = this.originalMethods[methodName];
        }
    }
}
