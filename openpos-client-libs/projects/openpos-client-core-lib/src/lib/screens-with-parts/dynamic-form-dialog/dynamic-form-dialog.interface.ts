import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';

export interface DynamicFormDialogInterface extends IAbstractScreen {

    submitButton: IActionItem;
    alternateSubmitActions: string[];

}
