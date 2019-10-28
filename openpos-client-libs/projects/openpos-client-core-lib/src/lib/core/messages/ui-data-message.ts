import {OpenposMessage} from './message';
import {MessageTypes} from './message-types';

export class UIDataMessage<T> implements OpenposMessage{
    constructor(public dataType: string, public seriesId: number, public data: T){}

    type = MessageTypes.DATA;
}