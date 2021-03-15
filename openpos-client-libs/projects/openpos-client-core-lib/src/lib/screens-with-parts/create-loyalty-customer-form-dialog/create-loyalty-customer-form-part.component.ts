import {
    Component
} from '@angular/core';
import { DynamicFormPartComponent } from "../../shared/screen-parts/dynamic-form-part/dynamic-form-part.component";
import {Observable} from "rxjs";

@Component({
    selector: 'app-create-loyalty-customer-form-part',
    templateUrl: './create-loyalty-customer-form-part.component.html',
    styleUrls: ['./create-loyalty-customer-form-part.component.scss']
})
export class CreateLoyaltyCustomerFormPart extends DynamicFormPartComponent {

    isMobile: Observable<boolean>;

    ngOnInit() {
        super.ngOnInit();
        console.log("Did the ngOnInit stuff. Good job");
    }
}