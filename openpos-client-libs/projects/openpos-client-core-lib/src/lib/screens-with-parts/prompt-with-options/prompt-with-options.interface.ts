import { IOptionItem } from '../../screens-deprecated/choose-options/option-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';

export interface PromptWithOptionsInterface extends IAbstractScreen {
    options: IOptionItem[];
    sausageLinks: IActionItem[];
}
