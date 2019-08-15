import { ConfigChangedMessage } from './config-changed-message';

export class VersionsChangedMessage extends ConfigChangedMessage {
    versions: any;
}
