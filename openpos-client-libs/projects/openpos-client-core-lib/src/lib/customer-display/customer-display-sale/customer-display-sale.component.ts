import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';

@ScreenComponent({
    name: 'CustomerDisplaySale'
})
@Component({
    selector: 'app-customer-display-sale',
    templateUrl: './customer-display-sale.component.html',
    styleUrls: ['./customer-display-sale.component.scss']

})
export class CustomerDisplaySaleComponent extends PosScreen<any> {
    buildScreen() {
    }
}
