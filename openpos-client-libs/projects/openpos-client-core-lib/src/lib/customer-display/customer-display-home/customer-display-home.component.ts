import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';

@ScreenComponent({
    name: 'CustomerDisplayHome'
})
@Component({
    selector: 'app-customer-display-home',
    templateUrl: './customer-display-home.component.html',
    styleUrls: ['./customer-display-home.component.scss']

})
export class CustomerDisplayHomeComponent extends PosScreen<any> {
    buildScreen() {
    }
}
