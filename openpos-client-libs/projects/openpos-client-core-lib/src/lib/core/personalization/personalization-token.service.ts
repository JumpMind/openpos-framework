import { Injectable } from '@angular/core';
import { DiscoveryService } from '../discovery/discovery.service';
import { PersonalizationService } from './personalization.service';

@Injectable({
    providedIn: 'root'
})
export class PersonalizationTokenService {
    static readonly BASE_URL_TOKEN = '${apiServerBaseUrl}';
    static readonly APP_ID_TOKEN = '${appId}';
    static readonly DEVICE_ID_TOKEN = '${deviceId}';

    constructor(private discoveryService: DiscoveryService, private personalizationService: PersonalizationService) {
    }

    public replaceTokens(value: string): string {
        if(!value) {
            return value;
        }

        const apiServerBaseUrl = this.discoveryService.getApiServerBaseURL();
        const deviceId = this.personalizationService.getDeviceId$().getValue();
        const appId = this.personalizationService.getAppId$().getValue();

        let url = value.replace(PersonalizationTokenService.BASE_URL_TOKEN, apiServerBaseUrl);
        url = url.replace(PersonalizationTokenService.APP_ID_TOKEN, appId);

        return url.replace(PersonalizationTokenService.DEVICE_ID_TOKEN, deviceId);
    }
}