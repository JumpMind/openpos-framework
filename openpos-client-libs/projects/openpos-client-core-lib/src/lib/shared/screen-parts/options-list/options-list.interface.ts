import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface OptionsListInterface extends IAbstractScreen {
    options: IActionItem[];
    additionalButtons: IActionItem[];
    linkButtons: IActionItem[];
    overflowButton: IActionItem;
    firstElementFocused: boolean
}
