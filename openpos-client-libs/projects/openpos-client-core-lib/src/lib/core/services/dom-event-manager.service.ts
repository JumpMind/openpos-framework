import { Injectable } from '@angular/core';
import { Observable, fromEvent, merge } from 'rxjs';
import { FromEventTarget } from 'rxjs/internal/observable/fromEvent';

@Injectable({
    providedIn: 'root'
})
export class DomEventManager {
    createEventObserver( target: FromEventTarget<Event>, eventName: string | string[], args?: EventListenerOptions ): Observable<Event> {
        if ( Array.isArray(eventName) ) {
            return merge(...eventName.map( e => fromEvent( target, e, args )));
        }

        return fromEvent( target, eventName, args);
    }

    dispatchEvent( target: EventTarget, event: Event ): boolean {
       return target.dispatchEvent(event);
    }
}
