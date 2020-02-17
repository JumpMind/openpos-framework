import {Inject, Injectable} from '@angular/core';
import {filter, tap} from 'rxjs/operators';
import {IActionItem} from '../actions/action-item.interface';
import {ActionMessage} from '../messages/action-message';
import {MessageTypes} from '../messages/message-types';
import {
    AndroidContentProviderPlugin,
    AndroidContentQuery
} from '../platform-plugins/cordova-plugins/android-content-provider-plugin';
import {SessionService} from '../services/session.service';
import {SignOnProviderNames} from './sign-on-provider-names';

@Injectable({
    providedIn: 'root'
})
export class BlueFletchAuthProvider {

    private responseAction: IActionItem;

    constructor( private sessionService: SessionService, private androidContentProviderPlugin: AndroidContentProviderPlugin) {
        console.log('BlueFletch Auth provider loaded');

        sessionService.getMessages(MessageTypes.SINGLE_SIGN_ON_REQUEST).pipe(
            filter( m => m.providerName === SignOnProviderNames.BLUE_FLETCH),
            tap( m => this.responseAction = m.responseAction),
            tap( m => console.log('BlueFletch sign on request received'))
        ).subscribe( m => this.querySession());
    }

    private querySession() {
        if( this.androidContentProviderPlugin.pluginPresent() ){
            this.androidContentProviderPlugin.query( new AndroidContentQuery("content://com.bluefletch.launcherprovider/session", ["data"]))
                .subscribe({
                    next: data => this.sendSignOnResponse(data[0]["DATA"]),
                    error: err =>  this.sessionService.sendMessage(new ActionMessage( this.responseAction.action, this.responseAction.doNotBlockForResponse))

                })
        } else {
            console.warn(`Android Content Provider not present`);
            this.sessionService.sendMessage(new ActionMessage( this.responseAction.action, this.responseAction.doNotBlockForResponse));
        }
    }

    private sendSignOnResponse( sessionData: string) {
        console.log(`responding with session data: ${sessionData}`);
        let session = JSON.parse(sessionData);
        if( session.extendedAttributes ) {
            session.extendedAttributes = JSON.parse(session.extendedAttributes);
        }
        this.sessionService.sendMessage(new ActionMessage( this.responseAction.action, this.responseAction.doNotBlockForResponse, JSON.stringify(session)));
    }
}