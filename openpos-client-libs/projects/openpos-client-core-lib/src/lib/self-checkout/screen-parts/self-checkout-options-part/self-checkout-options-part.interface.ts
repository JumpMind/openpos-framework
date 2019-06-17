import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/interfaces/action-item.interface';

export interface SelfCheckoutOptionsPartInterface extends IAbstractScreen {

    options: IActionItem[];
    additionalButtons: IActionItem[];
    linkButtons: IActionItem[];
}
