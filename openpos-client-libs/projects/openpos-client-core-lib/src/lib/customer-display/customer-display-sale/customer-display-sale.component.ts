import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component, OnInit, AfterViewChecked, ViewChild, ElementRef } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ISellItem } from '../../core/interfaces/sell-item.interface';

@ScreenComponent({
    name: 'CustomerDisplaySale'
})
@Component({
    selector: 'app-customer-display-sale',
    templateUrl: './customer-display-sale.component.html',
    styleUrls: ['./customer-display-sale.component.scss']

})
export class CustomerDisplaySaleComponent extends PosScreen<any> implements OnInit, AfterViewChecked {
    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;

    public size = -1;

    public items: ISellItem[];

    buildScreen() {
        this.items = this.screen.items;
    }

    ngAfterViewChecked() {
        if (this.items && this.size !== this.items.length) {
            this.scrollToBottom();
            this.size = this.items.length;
        }
    }

    scrollToBottom(): void {
        try {
            this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
        } catch (err) { }
    }

    ngOnInit(): void {
        this.scrollToBottom();
    }
}
