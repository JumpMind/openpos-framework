import { Component, ViewChild, AfterViewChecked, ElementRef, OnInit } from '@angular/core';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

@ScreenComponent({
    name: 'SelfCheckoutSale'
  })
@Component({
    selector: 'app-self-checkout-sale',
    templateUrl: './self-checkout-sale.component.html',
    styleUrls: ['./self-checkout-sale.component.scss']
})
export class SelfCheckoutSaleComponent extends PosScreen<any> implements AfterViewChecked, OnInit {
    @ViewChild('scrollList') private scrollList: ElementRef;

    initialized = false;

    public items: ISellItem[];
    public size = -1;

    private loyaltyIconToken = '${icon}';
    public loyaltyBefore: string;
    public loyaltyAfter: string;

    buildScreen() {
        this.items = this.screen.items;

        if (this.screen.loyaltyButton) {
            const title = this.screen.loyaltyButton.title as string;
            const parts = title.split(this.loyaltyIconToken);
            if (parts && parts.length > 0) {
                this.loyaltyBefore = parts[0].trim();
                if (parts.length > 1) {
                    this.loyaltyAfter = parts[1].trim();
                }
            }
        }
    }

    ngOnInit(): void {
        this.scrollToBottom();
    }

    ngAfterViewChecked() {
        if (this.items && this.size !== this.items.length) {
            this.scrollToBottom();
            this.size = this.items.length;
        }
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.doAction(menuItem);
    }

    scrollToBottom(): void {
        try {
            this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
        } catch (err) { }
    }

}
