import { BannerInterface, MessageType } from './banner.interface';
import { ScreenPartComponent } from '../screen-part';
import { Component, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';

@ScreenPart({
    name: 'banner'
})
@Component({
    selector: 'app-banner',
    templateUrl: './banner.component.html',
    styleUrls: ['./banner.component.scss'],
})
export class BannerComponent extends ScreenPartComponent<BannerInterface> {
    MessageType = MessageType;
    constructor(injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
    }
}