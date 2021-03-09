import {PosScreen} from "../../pos-screen/pos-screen.component";
import {IActionItem} from "../../../core/actions/action-item.interface";
import {DialogComponent} from "../../../shared/decorators/dialog-component.decorator";
import {Component} from "@angular/core";
import {AddItemToReturnTransactionInterface} from "./add-item-to-return-transaction.interface";
import {FormGroup} from "@angular/forms";

@DialogComponent({
    name: 'AddItemToTransactionDialog'
})
@Component({
    selector: 'app-add-item-to-return-transaction-dialog',
    templateUrl: './add-item-to-return-transaction-dialog.component.html',
    styleUrls: ['./add-item-to-return-transaction-dialog.component.scss']
})
export class AddItemToReturnTransactionComponent extends PosScreen<AddItemToReturnTransactionInterface> {
    addItemButton: IActionItem;
    form: FormGroup;

    buildScreen() {
        this.addItemButton = this.screen.addItemButton;
        this.form = this.screen.form;
    }

}
