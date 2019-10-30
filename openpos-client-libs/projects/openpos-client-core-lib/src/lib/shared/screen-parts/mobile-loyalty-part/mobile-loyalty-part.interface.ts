import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';


export interface MobileLoyaltyPartInterface extends IAbstractScreen {
    mobileLoyaltyButton: IActionItem;
    customer: { name: string, label: string, icon: string, id: string };
}
