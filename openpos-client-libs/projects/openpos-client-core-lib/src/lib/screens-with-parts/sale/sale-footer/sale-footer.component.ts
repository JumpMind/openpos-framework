import { Component } from '@angular/core';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { SaleFooterInterface } from './sale-footer.interface';

@Component({
    selector: 'app-sale-footer',
    templateUrl: './sale-footer.component.html',
    styleUrls: ['./sale-footer.component.scss']})
export class SaleFooterComponent extends ScreenPartComponent<SaleFooterInterface> {
    screenDataUpdated() {

    }
}
