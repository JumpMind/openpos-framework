import {Injectable} from '@angular/core';
import {SessionService} from './session.service';
import {DiscoveryService} from '../discovery/discovery.service';
import {PersonalizationService} from '../personalization/personalization.service';

@Injectable({
    providedIn: 'root',
})
export class ImageService {
    private baseUrlToken = '${apiServerBaseUrl}';
    private appIdToken = '${appId}';
    private deviceIdToken = '${deviceId}';

    constructor(private personalizer: PersonalizationService,
                private discovery: DiscoveryService, private session: SessionService) { }

    replaceImageUrl(originalUrl: string): string {
        if (originalUrl) {
            const apiServerBaseUrl = this.discovery.getApiServerBaseURL();
            const deviceId = this.personalizer.getDeviceId$().getValue();
            const appId = this.personalizer.getAppId$().getValue();

            let url = originalUrl.replace(this.baseUrlToken, apiServerBaseUrl);
            url = url.replace(this.appIdToken, appId);
            url = url.replace(this.deviceIdToken, deviceId);
            return url;
        } else {
            return originalUrl;
        }

    }

}


