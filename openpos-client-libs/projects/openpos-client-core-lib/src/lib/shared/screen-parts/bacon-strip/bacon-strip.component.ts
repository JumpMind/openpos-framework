import { BaconStripInterface } from './bacon-strip.interface';
import { ScreenPartComponent } from '../screen-part';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';
import { Observable } from 'rxjs';
import { Component } from '@angular/core';
import { MessageProvider } from '../../providers/message.provider';
import { ScreenPart } from '../../decorators/screen-part.decorator';

@ScreenPart({
    name: 'baconStrip'})
@Component({
    selector: 'app-bacon-strip',
    templateUrl: './bacon-strip.component.html',
    styleUrls: ['./bacon-strip.component.scss']})
export class BaconStripComponent extends ScreenPartComponent<BaconStripInterface> {

    isMobile$: Observable<boolean>;
    operatorInfo: string;

    constructor( mediaService: OpenposMediaService, messageProvider: MessageProvider) {
        super(messageProvider);
        const mobileMap = new Map([
            ['xs', true],
            ['sm', false],
            ['md', false],
            ['lg', false],
            ['xl', false]
        ]);
        this.isMobile$ = mediaService.mediaObservableFromMap(mobileMap);
    }

    screenDataUpdated() {
        if (this.screenData.backButton) {
            this.screenData.backButton.keybind = 'Escape';
        }
        if (this.screenData.operatorText && this.screenData.deviceId ) {
            this.operatorInfo = this.screenData.operatorText + ' on ' + this.screenData.deviceId;
        } else {
            this.operatorInfo = this.screenData.operatorText ? this.screenData.operatorText : this.screenData.deviceId;
        }
    }
}
