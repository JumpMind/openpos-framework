import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';


export interface CustomerDisplayFormInterface extends IAbstractScreen {
    submitButton: IActionItem;
    alternateSubmitActions: IActionItem[];
    imageUrl: string;
    instructions: string;
    autoComplete: boolean;
}
