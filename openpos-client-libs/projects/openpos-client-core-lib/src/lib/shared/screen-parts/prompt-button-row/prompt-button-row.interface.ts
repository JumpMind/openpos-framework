import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';


export interface PromptButtonRowInterface extends IAbstractScreen {
    primaryButton: IActionItem;
    secondaryButtons: IActionItem[];
    warnButtons: IActionItem[];
}
