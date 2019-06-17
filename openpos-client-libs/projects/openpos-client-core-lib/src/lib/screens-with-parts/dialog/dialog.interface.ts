import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { ILine } from '../../screens-deprecated/dialog/line.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';

export interface DialogInterface extends IAbstractScreen {
    buttons: IActionItem[];
    message: string[];
    messageLines: ILine[];
}
