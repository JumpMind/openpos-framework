import { Component, Inject } from '@angular/core';
import { IOrderSummary } from '../../../core/interfaces/order-summary.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material';

@Component({
    selector: 'app-mobile-sale-orders-sheet',
    templateUrl: './mobile-sale-orders-sheet.component.html',
    styleUrls: ['./mobile-sale-orders-sheet.component.scss']
})

export class MobileSaleOrdersSheetComponent {

    public orders: IOrderSummary[];
    public removeOrderAction: IActionItem;

    constructor( private ref: MatBottomSheetRef<MobileSaleOrdersSheetComponent>,
                 @Inject(MAT_BOTTOM_SHEET_DATA) public data: any) {
        this.orders = this.data.orders;
        this.removeOrderAction = this.data.removeOrderAction;
    }

    public onOrderClick(event: any) {
        if (this.orders) {
            const index = this.orders.indexOf(event);
            this.ref.dismiss(index);
        }
    }

    public onOrderRemove(event: IOrderSummary) {
        this.ref.dismiss(event);
    }

    public closeSheet() {
        this.ref.dismiss();
    }

}
