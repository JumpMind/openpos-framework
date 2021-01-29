import { Component } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { MobileEmployeePartInterface } from './mobile-employee-part.interface';


@ScreenPart({
    name: 'MobileEmployeePart'
})
@Component({
    selector: 'app-mobile-employee-part',
    templateUrl: './mobile-employee-part.component.html',
    styleUrls: ['./mobile-employee-part.component.scss']
})
export class MobileEmployeePartComponent extends ScreenPartComponent<MobileEmployeePartInterface> {

    screenDataUpdated() {
    }

}
