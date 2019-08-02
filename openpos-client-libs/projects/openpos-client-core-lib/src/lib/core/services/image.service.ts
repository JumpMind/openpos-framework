import { Injectable } from '@angular/core';
import { PersonalizationService } from '../personalization/personalization.service';
import { SessionService } from './session.service';

@Injectable({
    providedIn: 'root',
})
export class ImageService {
    private baseUrlToken = '${apiServerBaseUrl}';
    private appIdToken = '${appId}';
    private deviceIdToken = '${deviceId}';

    constructor(private personalizationService: PersonalizationService, private session: SessionService) { }

    replaceImageUrl(originalUrl: string): string {
        const apiServerBaseUrl = this.personalizationService.getApiServerBaseURL();
        const deviceId = this.personalizationService.getDeviceId();
        const appId = this.session.getAppId();

        let url = originalUrl.replace(this.baseUrlToken, apiServerBaseUrl);
        url = url.replace(this.appIdToken, appId);
        url = url.replace(this.deviceIdToken, deviceId);

        return url;
    }

}


