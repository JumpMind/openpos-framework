import { LogMethodType } from './log-message-type.enum';

export interface ILogAppender {
    // Modifies the original message and returns a new one.
    append( message: string, messageType: LogMethodType ): string ;
}
