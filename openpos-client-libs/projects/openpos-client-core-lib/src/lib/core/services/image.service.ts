import {Injectable} from '@angular/core';
import {SessionService} from './session.service';
import {DiscoveryService} from '../discovery/discovery.service';
import {PersonalizationService} from '../personalization/personalization.service';
import {filter, map, take} from "rxjs/operators";

@Injectable({
    providedIn: 'root',
})
export class ImageService {
    private baseUrlToken = '${apiServerBaseUrl}';
    private appIdToken = '${appId}';
    private deviceIdToken = '${deviceId}';
    private _imageNotFoundURL;

    get imageNotFoundURL(){
        return this._imageNotFoundURL;
    }

    constructor(private personalizer: PersonalizationService,
                private discovery: DiscoveryService, private session: SessionService) {
        this.setImageNotFoundURL();
    }

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

    setImageNotFoundURL(){
        this.session.getMessages('ConfigChanged')
            .pipe(
                filter(message => message.configType === 'MediaService'),
                map(res => res["image-not-found"] ),
                take(1)
            ).subscribe(res => this._imageNotFoundURL = res);
    }

}


