import { Component, ViewChild, AfterViewChecked, ElementRef, Injector } from '@angular/core';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { DeviceService } from '../../core/services/device.service';
import { Observable } from 'rxjs';
import { SaleItemCardListComponent } from '../../shared/screen-parts/sale-item-card-list/sale-item-card-list.component';
import { map } from 'rxjs/operators';

@ScreenComponent({
    name: 'SelfCheckoutSale'
})
@Component({
    selector: 'app-self-checkout-sale',
    templateUrl: './self-checkout-sale.component.html',
    styleUrls: ['./self-checkout-sale.component.scss']
})
export class SelfCheckoutSaleComponent extends PosScreen<any> implements AfterViewChecked {
    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;
    @ViewChild('scrollList') private saleItemCardList: SaleItemCardListComponent;

    initialized = false;

    public items: ISellItem[];
    public size = -1;

    constructor(public devices: DeviceService, injector: Injector) {
        super(injector);
    }

    buildScreen() {
        this.items = this.screen.items;
    }

    ngAfterViewChecked() {
        if (this.items && this.size !== this.items.length) {
            this.scrollToBottom();
            this.size = this.items.length;
        }
    }

    hasItems(): Observable<boolean> {
        return !!this.saleItemCardList.items$ && this.saleItemCardList.items$.pipe(map(items => !!items && items.length > 0));
    }

    scrollToBottom(): void {
        try {
            this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
        } catch (err) { }
    }
}
