import { Component } from '@angular/core';
import {LockScreenService} from '../../../core/lock-screen/lock-screen.service';
import {OpenposMediaService} from '../../../core/media/openpos-media.service';
import {PersonalizationService} from '../../../core/personalization/personalization.service';
import {FetchMessageService} from '../../../core/services/fetch-message.service';
import {LocaleService} from '../../../core/services/locale.service';
import {LocationService} from '../../../core/services/location.service';

@Component({
    selector: 'app-openpos-root',
    templateUrl: './openpos-app.component.html'
})
export class OpenposAppComponent {
    constructor(
        protected personalization: PersonalizationService,
        protected localeService: LocaleService,
        protected locationService: LocationService,
        protected mediaService: OpenposMediaService,
        protected lockScreenService: LockScreenService,
        protected fetchMessageService: FetchMessageService
    ) {
    }
}
