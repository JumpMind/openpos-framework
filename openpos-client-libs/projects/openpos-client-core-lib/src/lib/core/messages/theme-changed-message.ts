import { ConfigChangedMessage } from './config-changed-message';

export class ThemeChangedMessage extends ConfigChangedMessage {
    name: string;
}
