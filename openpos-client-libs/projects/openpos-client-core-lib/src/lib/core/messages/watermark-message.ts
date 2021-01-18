import { OpenposMessage } from './message';

export interface WatermarkMessage extends OpenposMessage {
    screenMessage: string;
}
