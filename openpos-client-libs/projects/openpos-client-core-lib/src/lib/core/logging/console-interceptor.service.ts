import { Injectable, InjectionToken, Optional, Inject } from '@angular/core';
import { ILogger } from './logger.interface';
import { SessionService } from '../services/session.service';
import { filter } from 'rxjs/operators';
import { ConsoleInterceptorConfig } from './console-interceptor-config';
import { MessageTypes } from '../messages/message-types';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';

export const LOGGERS = new InjectionToken<ILogger[]>('Loggers');

@Injectable({
    providedIn: 'root'
})
export class ConsoleIntercepter {

    private configuration: ConsoleInterceptorConfig;
    private originalMethods = new Map<string, any>();

    constructor( @Optional() @Inject(LOGGERS) private loggers: Array<ILogger>,
                 interceptorBypass: ConsoleInterceptorBypassService,
                 sessionService: SessionService ) {

        interceptorBypass.getMessages$().subscribe( m => {
            this.byPassInterceptor( m.method, m.message);
        });

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

    private byPassInterceptor( method: string, message: string ) {
        if ( this.originalMethods.has(method) ) {
            this.originalMethods.get(method).call(console, message);
        } else if ( !!console[method] ) {
            console[method](message);
        }
    }

    private intercept(methodName: string) {
        this.originalMethods.set(methodName, console[methodName]);
        console[methodName] = (args) => {

            this.loggers.forEach( logger => {
                logger[methodName](args);
            });
        };
    }

    private restore(methodName: string) {
        if (this.originalMethods.has(methodName)) {
            console[methodName] = this.originalMethods.get(methodName);
        }
    }
}
