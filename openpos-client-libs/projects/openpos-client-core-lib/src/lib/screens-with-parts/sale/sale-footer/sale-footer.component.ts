import { Component } from '@angular/core';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { SaleFooterInterface } from './sale-footer.interface';
import { MessageProvider } from '../../../shared/providers/message.provider';

@Component({
    selector: 'app-sale-footer',
    templateUrl: './sale-footer.component.html',
    styleUrls: ['./sale-footer.component.scss']})
export class SaleFooterComponent extends ScreenPartComponent<SaleFooterInterface> {

    constructor( messageProvider: MessageProvider) {
        super(messageProvider);
    }

    screenDataUpdated() {

    }
}
