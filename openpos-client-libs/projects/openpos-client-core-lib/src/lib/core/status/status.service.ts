import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {filter, tap} from 'rxjs/operators';
import {ConfigChangedMessage} from '../messages/config-changed-message';
import {MessageTypes} from '../messages/message-types';
import {StatusMessage} from './status.message';
import {SessionService} from '../services/session.service';

@Injectable({
    providedIn: 'root'
})
export class StatusService {

    private systemInfoConfig$ = new ReplaySubject<Map<string, string>>(1);
    private latestStatus = new Map<string, StatusMessage>();
    private latestStatus$ = new ReplaySubject<Map<string, StatusMessage>>(1);

    constructor( sessionService: SessionService) {
        sessionService.getMessages(MessageTypes.STATUS).pipe(
            tap(message => console.log("Status Updated", message))
        ).subscribe( message => this.statusUpdated(message));
        sessionService.getMessages(MessageTypes.CONFIG_CHANGED).pipe(
            filter( message => (message as ConfigChangedMessage).configType === 'SystemInfo'),
            tap( message => console.log( "SystemInfo Updated ", message))
        ).subscribe( message => this.configUpdated(message));
    }

    public getStatus(): Observable<Map<string, StatusMessage>>{
        return this.latestStatus$;
    }

    public getSystemInfo(): Observable<Map<string, string>> {
        return this.systemInfoConfig$;
    }

    private configUpdated( message: ConfigChangedMessage ){
        let properties =
            new Map(Object.entries(message)
                .filter( entry => entry[0] !== 'configType' && entry[0] !== 'type'));

        this.systemInfoConfig$.next(properties);
    }

    private statusUpdated( message: StatusMessage){
        this.latestStatus.set(message.id, message);

        this.latestStatus$.next(this.latestStatus);
    }
}