import {Component} from '@angular/core';
import {ScreenPartComponent} from '../screen-part';
import {MembershipPointsDisplayComponentInterface} from './membership-points-display.interface';

@Component({
    selector: 'app-membership-points-display',
    templateUrl: './membership-points-display.component.html',
    styleUrls: ['./membership-points-display.component.scss']})
export class MembershipPointsDisplayComponent extends ScreenPartComponent<MembershipPointsDisplayComponentInterface>{
    screenDataUpdated() {
    }
}
