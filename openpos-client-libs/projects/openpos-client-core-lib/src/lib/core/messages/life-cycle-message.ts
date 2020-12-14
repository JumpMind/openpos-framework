import { OpenposMessage } from './message';
import { MessageTypes } from './message-types';
import { LifeCycleEvents } from './life-cycle-events.enum';

export class LifeCycleMessage implements OpenposMessage {
    type = MessageTypes.LIFE_CYCLE_EVENT;
    eventType: LifeCycleEvents;

    constructor( eventType: LifeCycleEvents ) {
        this.eventType = eventType;
    }
}
