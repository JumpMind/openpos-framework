import {OpenposMessage} from './message';

export interface FetchMessage extends OpenposMessage {
    messageIdToFetch: string;
}