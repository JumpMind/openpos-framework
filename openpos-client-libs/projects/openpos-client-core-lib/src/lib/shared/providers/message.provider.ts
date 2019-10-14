import { Injectable } from '@angular/core';
import { SessionService } from '../../core/services/session.service';
import { Observable, ReplaySubject, Subscription} from 'rxjs';
import { OpenposMessage } from '../../core/messages/message';
import {map} from 'rxjs/operators';
import { MessageTypes } from '../../core/messages/message-types';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';

@Injectable({providedIn:'root'})
export class MessageProvider {

    //Create state for our messages
    private messages$ = new ReplaySubject<any>(1);
    private messageType: string;
    private subscription: Subscription;

    constructor( private sessionService: SessionService ) {
        //Default to Dialog for the root scope. The screen-outlet will create an instance with type screen
        this.setMessageType(MessageTypes.DIALOG);
    }

    setMessageType( messageType: string ) {
        if( this.subscription ) {
            this.subscription.unsubscribe();
        }
        // Update state withe the latest message
        this.subscription  = this.sessionService.getMessages( messageType ).subscribe( m => this.messages$.next(m));
        this.messageType = messageType;
    }

    getScopedMessages$<T extends OpenposMessage>(): Observable<T> {
        return this.messages$;
    }

    getAllMessages$<T extends OpenposMessage>(): Observable<T> {
        return this.sessionService.getMessages().pipe(
            map( m => {
                if ( m.type === MessageTypes.LIFE_CYCLE_EVENT ) {
                    const lfMessage = m as LifeCycleMessage;
                    switch ( lfMessage.eventType ) {
                        case LifeCycleEvents.DialogOpening:
                            return this.messageType === MessageTypes.DIALOG ?
                                new LifeCycleMessage(LifeCycleEvents.BecomingActive) :
                                new LifeCycleMessage(LifeCycleEvents.LeavingActive);
                        case LifeCycleEvents.DialogClosing:
                            return this.messageType === MessageTypes.DIALOG ?
                                new LifeCycleMessage(LifeCycleEvents.LeavingActive) :
                                new LifeCycleMessage(LifeCycleEvents.BecomingActive);
                    }
                }
                return m;
            })
        );
    }

    sendMessage<T extends OpenposMessage>( message: T) {
        this.sessionService.sendMessage(message);
    }
}
