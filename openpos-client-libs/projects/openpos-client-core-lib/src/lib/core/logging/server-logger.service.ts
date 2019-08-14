import { ILogger } from './logger.interface';
import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationService } from '../personalization/personalization.service';
import { ServerLogEntry } from './server-log-entry';
import { LogMethodType } from './log-method-type.enum';
import { Subject, of } from 'rxjs';
import { bufferTime, filter, catchError } from 'rxjs/operators';
import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';

@Injectable({
    providedIn: 'root'
})
export class ServerLogger implements ILogger, OnDestroy {

    private logBufferTime: number = 300;
    private loggerEndpointUrl: string;
    private logEntrySubject = new Subject<ServerLogEntry>();

    constructor(
        private http: HttpClient,
        personalizationService: PersonalizationService,
        private consoleInterceptorBypass: ConsoleInterceptorBypassService ) {
        personalizationService.getDeviceAppApiServerBaseUrl$().subscribe( url => this.loggerEndpointUrl = `${url}/clientlogs`);
        this.logEntrySubject.pipe(
            bufferTime( this.logBufferTime),
            filter( entries => entries.length > 0))
            .subscribe( entries => this.shipLogs(entries) );
    }

    ngOnDestroy(): void {
        this.logEntrySubject.complete();
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
