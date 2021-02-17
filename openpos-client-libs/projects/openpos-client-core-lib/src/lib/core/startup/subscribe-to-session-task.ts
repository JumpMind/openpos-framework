import { Injectable } from '@angular/core';
import { IStartupTask } from './startup-task.interface';
import { Observable, of, merge, throwError, Subject, concat } from 'rxjs';
import { SessionService } from '../services/session.service';
import { Router } from '@angular/router';
import { StartupTaskData } from './startup-task-data';
import { StartupTaskNames } from './startup-task-names';
import { map, take, timeoutWith } from 'rxjs/operators';
import { Configuration } from '../../configuration/configuration';
import { MessageTypes } from '../messages/message-types';

@Injectable()
export class SubscribeToSessionTask implements IStartupTask {
    name = StartupTaskNames.SUBSCRIBE_TO_SESSION;
    order = 600;

    constructor(
        protected session: SessionService,
        protected router: Router
    ) { }

    execute(data: StartupTaskData): Observable<string> {
        if (!this.session.connected()) {

            const subscribe = Observable.create((messages: Subject<string>) => {
                data.route.queryParamMap.keys.forEach(key => {
                    this.session.addQueryParam(key, data.route.queryParamMap.get(key));
                });

                this.session.unsubscribe();
                this.session.subscribe()
                    .then(() => {
                        messages.next('Subscribed to server');
                        messages.complete();
                    })
                    .catch(e => messages.error(e));
            }) as Observable<string>;

            return concat(
                subscribe,
                this.session.getMessages(MessageTypes.STARTUP).pipe(
                    timeoutWith(Configuration.confirmConnectionTimeoutMillis, throwError('Timed out waiting for server')),
                    map(() => 'Successfully connected to server'),
                    take(1)
                )
            );
        } else {
            // we shouldn't be coming here if we are already subscribed.  lets do a refresh to get a clean start
            window.location.reload();
        }
    }
}
