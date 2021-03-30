import {Component, Input} from '@angular/core';
import {CustomerDetails, CustomerInformationComponentInterface} from './customer-information.interface';
import {ScreenPartComponent} from '../screen-part';


@Component({
    selector: 'app-customer-information',
    templateUrl: './customer-information.component.html',
    styleUrls: ['./customer-information.component.scss']})
export class CustomerInformationComponent  extends ScreenPartComponent<CustomerInformationComponentInterface>{
    @Input()
    customer: CustomerDetails;

    screenDataUpdated() {
    }
}
