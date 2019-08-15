import { ILogger } from './logger.interface';
import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationService } from '../personalization/personalization.service';
import { ServerLogEntry } from './server-log-entry';
import { LogMethodType } from './log-method-type.enum';
import { Subject, of, Subscription } from 'rxjs';
import { bufferTime, filter, catchError } from 'rxjs/operators';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';
import { ConfigurationService } from '../services/configuration.service';
import { ServerLoggerConfiguration } from './server-logger-configuration';

@Injectable({
    providedIn: 'root'
})
export class ServerLogger implements ILogger, OnDestroy {

    private logBufferTime = 300;
    private loggerEndpointUrl: string;
    private logEntrySubject = new Subject<ServerLogEntry>();

    subscriptions = new Subscription();
    logEntrySubjectSubscription: Subscription;

    constructor(
        private http: HttpClient,
        personalizationService: PersonalizationService,
        private consoleInterceptorBypass: ConsoleInterceptorBypassService,
        configurationService: ConfigurationService ) {

        this.subscriptions.add(
            configurationService.getConfiguration<ServerLoggerConfiguration>('server-logger')
            .subscribe( m => {
                this.logBufferTime = m.logBufferTime;
                this.subscribeToLogSubject();
            })
        );

        this.subscriptions.add(
            personalizationService.getDeviceAppApiServerBaseUrl$()
            .subscribe( url => this.loggerEndpointUrl = `${url}/clientlogs`));

        this.subscribeToLogSubject();

    }

    private subscribeToLogSubject() {
        if (this.logEntrySubjectSubscription != null) {
            this.logEntrySubject.complete();
            this.logEntrySubjectSubscription.unsubscribe();
        }
        this.logEntrySubjectSubscription = this.logEntrySubject.pipe(
            bufferTime(this.logBufferTime),
            filter(entries => entries.length > 0))
            .subscribe(entries => this.shipLogs(entries) );
    }

    ngOnDestroy(): void {
        this.logEntrySubject.complete();
        this.logEntrySubjectSubscription.unsubscribe();
        this.subscriptions.unsubscribe();
    }

    log( message: string ) {
        this.logEntrySubject.next(new ServerLogEntry(LogMethodType.log, Date.now() , message));
    }

    info( message: string ) {
        this.logEntrySubject.next(new ServerLogEntry(LogMethodType.info, Date.now() , message));
    }
    error( message: string ) {
        this.logEntrySubject.next(new ServerLogEntry(LogMethodType.error, Date.now() , message));
    }
    warn( message: string ) {
        this.logEntrySubject.next(new ServerLogEntry(LogMethodType.warn, Date.now() , message));
    }
    debug( message: string ) {
        this.logEntrySubject.next(new ServerLogEntry(LogMethodType.debug, Date.now() , message));
    }

    private shipLogs( entries: ServerLogEntry[] ) {
        this.http.post(this.loggerEndpointUrl, entries).pipe(
            catchError( error => {
                // If we fail to send to the server dump them out to the console.
                entries.forEach( e => {
                    this.consoleInterceptorBypass[e.type](e.message);
                });
                return of(error);
            }))
            .subscribe( r => {
                if ( r != null) {
                    this.consoleInterceptorBypass.log(r);
                }});
    }

}
