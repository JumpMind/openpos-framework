import { Injectable } from '@angular/core';
import { SessionService } from '../../core/services/session.service';
import { Observable, ConnectableObservable } from 'rxjs';
import { Message } from '../../core/messages/message';
import { map, publishReplay } from 'rxjs/operators';
import { MessageTypes } from '../../core/messages/message-types';
import { LifeCycleMessage } from '../../core/messages/life-cycle-message';
import { LifeCycleEvents } from '../../core/messages/life-cycle-events.enum';

@Injectable()
export class MessageProvider {

    private messages$: Observable<any>;
    private messageType: string;

    constructor( private sessionService: SessionService ) {

    }

    setMessageType( messageType: string ) {
        this.messages$ = this.sessionService.getMessages( messageType ).pipe(publishReplay(1));
        (this.messages$ as ConnectableObservable<any>).connect();
        this.messageType = messageType;
    }

    getScopedMessages$<T extends Message>(): Observable<T> {
        return this.messages$;
    }

    getAllMessages$<T extends Message>(): Observable<T> {
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

    sendMessage<T extends Message>( message: T) {
        this.sessionService.sendMessage(message);
    }
}
