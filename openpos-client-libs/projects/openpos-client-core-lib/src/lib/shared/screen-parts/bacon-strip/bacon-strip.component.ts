import { BaconStripInterface } from './bacon-strip.interface';
import { ScreenPartComponent } from '../screen-part';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';
import { Observable } from 'rxjs';
import { Component, Injector } from '@angular/core';
import { MessageProvider } from '../../providers/message.provider';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { HelpTextService } from '../../../core/help-text/help-text.service';
import { TillStatusType } from '../../../core/interfaces/till-status-type.enum';

@ScreenPart({
    name: 'baconStrip'})
@Component({
    selector: 'app-bacon-strip',
    templateUrl: './bacon-strip.component.html',
    styleUrls: ['./bacon-strip.component.scss']})
export class BaconStripComponent extends ScreenPartComponent<BaconStripInterface> {

    isMobile$: Observable<boolean>;
    operatorInfo: string;
    TillStatusType = TillStatusType;

    constructor( mediaService: OpenposMediaService, injector: Injector, public helpTextService: HelpTextService) {
        super(injector);
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
            this.operatorInfo = this.screenData.operatorText + ' on ';
        } else {
            this.operatorInfo = this.screenData.operatorText;
        }
    }
}
