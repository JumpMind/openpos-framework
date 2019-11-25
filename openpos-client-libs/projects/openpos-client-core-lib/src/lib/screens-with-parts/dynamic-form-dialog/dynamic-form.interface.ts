import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
export interface DynamicFormInterface extends IAbstractScreen {
    instructions: string;
    submitButton: IActionItem;
    alternateSubmitActions: string[];
    imageUrl: string;
}
