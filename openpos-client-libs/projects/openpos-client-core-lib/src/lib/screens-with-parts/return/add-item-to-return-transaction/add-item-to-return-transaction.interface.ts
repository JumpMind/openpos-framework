import {IAbstractScreen} from "../../../core/interfaces/abstract-screen.interface";
import {IActionItem} from "../../../core/actions/action-item.interface";
import {FormGroup} from "@angular/forms";

export interface AddItemToReturnTransactionInterface extends IAbstractScreen {
    addItemButton: IActionItem;
    form: FormGroup;
}
