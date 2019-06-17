
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';

export interface PromptInterface extends IAbstractScreen {
    sausageLinks: IActionItem[];
}
