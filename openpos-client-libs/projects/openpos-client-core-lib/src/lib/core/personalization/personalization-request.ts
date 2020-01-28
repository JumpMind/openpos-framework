export class PersonalizationRequest {
    constructor(
        public deviceToken: string,
        public deviceId: string,
        public appId: string,
        public deviceType: string) {
    }

    public personalizationParameters = {};
}