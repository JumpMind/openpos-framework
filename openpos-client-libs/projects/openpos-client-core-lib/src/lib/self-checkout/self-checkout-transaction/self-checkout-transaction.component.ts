import { Component, ViewChild, AfterViewChecked, ElementRef, OnInit } from '@angular/core';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';

@Component({
    selector: 'app-self-checkout-transaction',
    templateUrl: './self-checkout-transaction.component.html',
    styleUrls: ['./self-checkout-transaction.component.scss']
})
export class SelfCheckoutTransactionComponent extends PosScreen<any> implements AfterViewChecked, OnInit {
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
