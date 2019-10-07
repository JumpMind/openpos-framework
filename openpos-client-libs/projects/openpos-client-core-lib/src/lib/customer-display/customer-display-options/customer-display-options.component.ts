import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { CustomerDisplayOptionsInterface } from './customer-display-options.interface';


@ScreenComponent({
    name: 'CustomerDisplayOptions'
})
@Component({
    selector: 'app-customer-display-options',
    templateUrl: './customer-display-options.component.html',
    styleUrls: ['./customer-display-options.component.scss']
})
export class CustomerDisplayOptionsComponent extends PosScreen<CustomerDisplayOptionsInterface> {

    buildScreen() {
    }

}
