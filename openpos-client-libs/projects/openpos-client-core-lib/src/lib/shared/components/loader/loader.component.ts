import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { LoaderState } from './loader-state';
import { SessionService } from '../../../core/services/session.service';
import { ILoading } from '../../../core/interfaces/loading.interface';
import { Logger } from '../../../core/services/logger.service';
import { IMessageHandler } from '../../../core/interfaces/message-handler.interface';
import { ConfigurationService } from '../../../core/services/configuration.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-loader',
    templateUrl: 'loader.component.html',
    styleUrls: ['loader.component.scss']
})
export class LoaderComponent implements OnInit, OnDestroy, IMessageHandler<ILoading> {
    show = false;
    title: string = LoaderState.LOADING_TITLE;
    message: string = null;
    time: Date = new Date();
    reconnecting = false;
    interval: any;

    loading = false;

    constructor(
        private log: Logger,
        private configuration: ConfigurationService,
        private session: SessionService,
        private changeRef: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.session.registerMessageHandler(this, 'Loading');
    }

    handle(message: ILoading) {
        this.log.info(`received loading message.  queue: ${message.queue}, cancel: ${message.cancel}, title: ${message.title}`);
        if (message.queue) {
            this.loading = true;
            setTimeout(() => {
                this.updateLoading(message, false);
            }, 1000);
        } else {
            this.updateLoading(message, true);
        }
    }

    private updateLoading(message: ILoading, force: boolean) {
        let stateChanging = false;
        if (message.cancel) {
            stateChanging = this.loading;
            this.loading = false;
            this.show = false;
        } else if ((this.loading && !this.show) || force) {
            stateChanging = !this.show;
            if (message.title === LoaderState.DISCONNECTED_TITLE) {
                this.startClock();
            } else {
                this.stopClock();
            }
            this.log.info(`showing the loading dialog NOW with a title of: ${message.title} `);
            this.loading = true;
            this.title = message.title;
            this.message = message.message;
            this.show = true;
        }

        if (stateChanging) {
            this.changeRef.detectChanges();
        }
    }

    startClock() {
        this.reconnecting = true;
        this.interval = setInterval(() => {
            this.time = new Date();
          }, 1);
    }

    stopClock() {
        this.reconnecting = false;
        if (this.interval) {
            clearInterval(this.interval);
        }
    }

    ngOnDestroy() {
    }

    public getHiddenClass(): string {
        if (this.show) {
            return '';
        } else {
            return 'loader-hidden';
        }
    }

    public getLocalTheme(): Observable<string> {
        return this.configuration.theme$;
    }
}
