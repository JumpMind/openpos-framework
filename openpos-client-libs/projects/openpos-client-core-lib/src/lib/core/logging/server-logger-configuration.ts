import { ConfigChangedMessage } from '../messages/config-changed-message';

export class ServerLoggerConfiguration extends ConfigChangedMessage {
    constructor(public logBufferTime: number) {
        super('server-logger');
    }
}
