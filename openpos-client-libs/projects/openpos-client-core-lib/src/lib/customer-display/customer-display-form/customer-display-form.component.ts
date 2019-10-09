import { Component } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { CustomerDisplayFormInterface } from './customer-display-form.interface';


@ScreenComponent({
    name: 'CustomerDisplayForm'
})
@Component({
    selector: 'app-customer-display-form',
    templateUrl: './customer-display-form.component.html',
    styleUrls: ['./customer-display-form.component.scss']
})
export class CustomerDisplayFormComponent extends PosScreen<CustomerDisplayFormInterface> {
    buildScreen() { }
}
