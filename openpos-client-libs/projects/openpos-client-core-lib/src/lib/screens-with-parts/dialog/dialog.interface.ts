import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface ILine {
    message: string;
    cssClass: string;
}

export interface DialogInterface extends IAbstractScreen {
    buttons: IActionItem[];
    message: string[];
    messageLines: ILine[];
}
