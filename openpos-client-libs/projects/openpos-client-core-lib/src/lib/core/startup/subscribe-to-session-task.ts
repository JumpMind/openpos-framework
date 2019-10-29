import { Injectable } from '@angular/core';
import { IStartupTask } from './startup-task.interface';
import { Observable, Subject, timer, Subscription, from, of, concat } from 'rxjs';
import { SessionService } from '../services/session.service';
import { Router } from '@angular/router';
import { Logger } from '../services/logger.service';
import { StartupTaskData } from './startup-task-data';
import { StartupTaskNames } from './startup-task-names';
import { takeUntil, map, filter } from 'rxjs/operators';

@Injectable()
export class SubscribeToSessionTask implements IStartupTask {
    name = StartupTaskNames.SUBSCRIBE_TO_SESSION;
    order = 600;

    private connectionTimeoutSubscr: Subscription;

    constructor(
        protected session: SessionService,
        protected router: Router,
        protected log: Logger
    ) { }

    execute(data: StartupTaskData): Observable<string> {
        const messages: string[] = [];
        if (!this.session.connected()) {

            if (!this.session.getAppId()) {
                let appId = data.state.url.substring(1);
                this.log.info('calculating app id from ' + appId);
                if (appId.indexOf('?') > 0) {
                    appId = appId.substring(0, appId.indexOf('?'));
                }
                if (appId.indexOf('/') > 0) {
                    appId = appId.substring(appId.lastIndexOf('/') + 1);
                }
                this.session.setAppId(appId);
            }

            data.route.queryParamMap.keys.forEach(key => {
                this.session.addQueryParam(key, data.route.queryParamMap.get(key));
            });

            messages.push(`[StartupService] Subscribing to server using appId '${this.session.getAppId()}'...`);
            this.session.unsubscribe();

            return Observable.create((result: Subject<string>) => {
                concat(
                    concat(
                        ...messages.map(m => of(m)),
                        from(this.session.subscribe()).pipe(
                            map(x => '')
                        )
                    ).pipe(filter(x => x !== '')),
                    this.confirmConnection().pipe(map(success => {
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

    protected confirmConnection(maxWaitMillis = 7500): Observable<boolean> {
        return Observable.create((result: Subject<boolean>) => {
            this.connectionTimeoutSubscr = timer(0, 500).pipe(takeUntil(timer(maxWaitMillis))).subscribe(
                x => {
                    if (this.session.connected()) {
                        this.log.info(`Session connection confirmed`);
                        this.connectionTimeoutSubscr.unsubscribe();
                        result.next(true);
                        result.complete();
                    } else {
                        this.log.info(`Session not connected yet`);
                    }
                },
                (err) => {
                    this.connectionTimeoutSubscr.unsubscribe();
                    result.next(false);
                    result.complete();
                },
                () => {
                    this.connectionTimeoutSubscr.unsubscribe();
                    this.log.error(`Timed out waiting for connection to be established`);
                    result.next(false);
                    result.complete();
                }
            );
        });
    }
}
