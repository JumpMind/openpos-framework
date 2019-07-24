import { IOptionItem } from '../../core/interfaces/option-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface PromptWithOptionsInterface extends IAbstractScreen {
    options: IOptionItem[];
    sausageLinks: IActionItem[];
}
