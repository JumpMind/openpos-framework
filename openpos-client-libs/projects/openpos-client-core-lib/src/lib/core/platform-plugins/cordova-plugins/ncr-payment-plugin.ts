import { IPlatformPlugin } from '../platform-plugin.interface';
import { Observable, Subject } from 'rxjs';
import { filter } from 'rxjs/operators';
import { SessionService } from '../../services/session.service';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class NCRPaymentPlugin implements IPlatformPlugin {

    private NCRCordovaPlugin;

    constructor(private sessionService: SessionService) { }

    name(): string {
        return 'NCRPaymentPlugin';
    }

    pluginPresent(): boolean {
        return !!window.hasOwnProperty('NCRCordovaPlugin');
    }

    initialize(): Observable<string> {
        return Observable.create((initialized: Subject<string>) => {
            // tslint:disable-next-line:no-string-literal
            this.NCRCordovaPlugin = window['NCRCordovaPlugin'];
            if (!this.NCRCordovaPlugin) {
                initialized.error(`Tried to initialize plugin ${this.name()} which is not present`);
            }

            this.sessionService.getMessages('Proxy').pipe(
                filter(m => m.proxyType === 'Payment')
            ).subscribe(message => {
                this.forwardMessage(message);
            });

            initialized.next(`Successfully intitialized ${this.name()}`);
            initialized.complete();
        });
    }

    forwardMessage(message: any) {
        if (message.action === 'ActivateDevice') {
            this.NCRCordovaPlugin.activateDevice(
                response => { this.handleSuccess(response); },
                response => { this.handleError(response); }
            );
        } else if (message.action === 'ProcessMessage') {
            this.NCRCordovaPlugin.processMessage(message.payload,
                response => { this.handleSuccess(response); },
                response => { this.handleError(response); }
            );
        }
    }

    handleSuccess(response: string) {
        console.log('SUCCESSFUL RESPONSE: ' + response);
    }

    handleError(response: string) {
        console.log('ERROR RESPONSE: ' + response);
    }

}
