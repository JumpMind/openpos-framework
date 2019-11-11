import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material';
import { Inject, Component } from '@angular/core';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ITransactionReceipt } from '../../../shared/components/receipt-card/transaction-receipt.interface';

@Component({
    selector: 'app-mobile-return-receipts-sheet',
    templateUrl: './mobile-return-receipts-sheet.component.html',
    styleUrls: ['./mobile-return-receipts-sheet.component.scss']
})

export class MobileReturnReceiptsSheetComponent {

    public receipts: ITransactionReceipt[];
    public removeReceiptAction: IActionItem;

    constructor( private ref: MatBottomSheetRef<MobileReturnReceiptsSheetComponent>,
                 @Inject(MAT_BOTTOM_SHEET_DATA) public data: any) {
        this.receipts = this.data.receipts;
        this.removeReceiptAction = this.data.removeReceiptAction;
    }

    public onReceiptClick(event: any) {
        if (this.receipts) {
            const index = this.receipts.indexOf(event);
            this.ref.dismiss(index);
        }
    }

    public onReceiptRemove(event: ITransactionReceipt) {
        this.ref.dismiss(event);
    }

    public closeSheet() {
        this.ref.dismiss();
    }

}
