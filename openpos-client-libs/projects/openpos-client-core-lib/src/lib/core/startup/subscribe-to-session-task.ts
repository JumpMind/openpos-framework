import { Injectable } from '@angular/core';
import { IStartupTask } from './startup-task.interface';
import { Observable, Subject, timer, Subscription, from, of, concat } from 'rxjs';
import { SessionService } from '../services/session.service';
import { Router } from '@angular/router';
import { StartupTaskData } from './startup-task-data';
import { StartupTaskNames } from './startup-task-names';
import { takeUntil, map, filter } from 'rxjs/operators';
import { Configuration } from '../../configuration/configuration';
import { MessageTypes } from '../messages/message-types';

@Injectable()
export class SubscribeToSessionTask implements IStartupTask {
    name = StartupTaskNames.SUBSCRIBE_TO_SESSION;
    order = 600;

    private connectionTimeoutSubscr: Subscription;
    private startupMessageRecv = false;

    constructor(
        protected session: SessionService,
        protected router: Router
    ) { }

    execute(data: StartupTaskData): Observable<string> {
        const messages: string[] = [];
        if (!this.session.connected()) {

            data.route.queryParamMap.keys.forEach(key => {
                this.session.addQueryParam(key, data.route.queryParamMap.get(key));
            });

            messages.push(`[StartupService] Subscribing to server ...`);
            this.session.unsubscribe();

            return Observable.create((result: Subject<string>) => {
                concat(
                    concat(
                        ...messages.map(m => of(m)),
                        from(this.session.subscribe()).pipe(
                            map(x => '')
                        )
                    ).pipe(filter(x => x !== '')),
                    this.confirmConnection(Configuration.confirmConnectionTimeoutMillis).pipe(map(success => {
                        if (success) { return 'Connection established'; }
                        else { throw new Error(`A connection to the server could not be established.`); }
                    }))
                ).subscribe(
                    x => result.next(x),
                    err => result.error(err && err.hasOwnProperty('message') ? err.message : err),
                    () => result.complete()
                );
            });
        } else {
            // we shouldn't be coming here if we are already subscribed.  lets do a refresh to get a clean start
            window.location.reload();
        }
    }

    protected subscribeToStartup(): void {
        this.startupMessageRecv = false;
        this.session.getMessages(MessageTypes.STARTUP).subscribe(
            m => this.handleStartupMessage(m)
        );
    }

    handleStartupMessage(message: any) {
        this.startupMessageRecv = true;
    }

    protected confirmConnection(maxWaitMillis = 7500): Observable<boolean> {
        this.subscribeToStartup();
        return Observable.create((result: Subject<boolean>) => {
            this.connectionTimeoutSubscr = timer(0, 500).pipe(takeUntil(timer(maxWaitMillis))).subscribe(
                x => {
                    if (this.session.connected() && this.startupMessageRecv) {
                        console.info(`Session connection confirmed`);
                        this.connectionTimeoutSubscr.unsubscribe();
                        result.next(true);
                        result.complete();
                    } else {
                        console.info(`Session not connected yet`);
                    }
                },
                (err) => {
                    this.connectionTimeoutSubscr.unsubscribe();
                    result.next(false);
                    result.complete();
                },
                () => {
                    this.connectionTimeoutSubscr.unsubscribe();
                    console.error(`Timed out waiting for connection to be established`);
                    result.next(false);
                    result.complete();
                }
            );
        });
    }
}
