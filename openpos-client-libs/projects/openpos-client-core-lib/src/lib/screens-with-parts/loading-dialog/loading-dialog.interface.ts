import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface LoadingDialogInterface extends IAbstractScreen {
   message: string;
   cancelButton: IActionItem;
}
