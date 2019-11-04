import { Component } from '@angular/core';
import { MobileLoyaltyPartInterface } from './mobile-loyalty-part.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';


@ScreenPart({
    name: 'MobileLoyaltyPart'
})
@Component({
    selector: 'app-mobile-loyalty-part',
    templateUrl: './mobile-loyalty-part.component.html',
    styleUrls: ['./mobile-loyalty-part.component.scss']
})
export class MobileLoyaltyPartComponent extends ScreenPartComponent<MobileLoyaltyPartInterface> {

    screenDataUpdated() {
    }

}
