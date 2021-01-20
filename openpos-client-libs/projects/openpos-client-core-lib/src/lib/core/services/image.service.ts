import { Injectable } from '@angular/core';
import { SessionService } from './session.service';
import { PersonalizationService } from '../personalization/personalization.service';
import { filter, map, take } from 'rxjs/operators';
import { PersonalizationTokenService } from '../personalization/personalization-token.service';

@Injectable({
    providedIn: 'root',
})
export class ImageService {
    private _imageNotFoundURL;

    get imageNotFoundURL() {
        return this._imageNotFoundURL;
    }

    constructor(private personalizationTokenService: PersonalizationTokenService, private session: SessionService) {
        this.setImageNotFoundURL();
    }

    replaceImageUrl(originalUrl: string): string {
        return this.personalizationTokenService.replaceTokens(originalUrl);
    }

    setImageNotFoundURL() {
        this.session.getMessages('ConfigChanged')
            .pipe(
                filter(message => message.configType === 'ImageService'),
                map(res => res["image-not-found"]),
                take(1)
            ).subscribe(res => this._imageNotFoundURL = res);
    }
}


