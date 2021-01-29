import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface MobileEmployeePartInterface extends IAbstractScreen {
    linkedEmployeeButton: IActionItem;
    employee: { name: string, label: string, icon: string, id: string };
}
