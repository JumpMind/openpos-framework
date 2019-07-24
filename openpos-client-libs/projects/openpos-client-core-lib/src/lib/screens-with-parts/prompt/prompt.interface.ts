
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface PromptInterface extends IAbstractScreen {
    sausageLinks: IActionItem[];
}
