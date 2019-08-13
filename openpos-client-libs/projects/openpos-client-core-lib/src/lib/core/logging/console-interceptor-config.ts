import { ConfigChangedMessage } from '../messages/config-changed-message';

export class ConsoleInterceptorConfig extends ConfigChangedMessage {
    enable: boolean;

    constructor() {
        super('ConsoleInterceptor');
    }
}
