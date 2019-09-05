import { Injectable } from '@angular/core';
import { IStartupTask } from './startup-task.interface';
import { Observable, Subject } from 'rxjs';
import { SessionService } from '../services/session.service';
import { Router } from '@angular/router';
import { Logger } from '../services/logger.service';
import { StartupTaskData } from './startup-task-data';
import { StartupTaskNames } from './startup-task-names';

@Injectable()
export class SubscribeToSessionTask implements IStartupTask {
    name = StartupTaskNames.SUBSCRIBE_TO_SESSION;
    order = 600;

    constructor(
        protected session: SessionService,
        protected router: Router,
        protected log: Logger
    ) { }

    execute(data: StartupTaskData): Observable<string> {
        return Observable.create((message: Subject<string>) => {
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

                message.next(`[StartupService] Subscribing to server using appId '${this.session.getAppId()}'...`);
                this.session.unsubscribe();
                this.session.subscribe();
                message.complete();
            } else {
                /* we shouldn't be coming there here if we are already subscribed.  lets do a refresh to get a clean start */
                window.location.reload();
            }
        });
    }
}
