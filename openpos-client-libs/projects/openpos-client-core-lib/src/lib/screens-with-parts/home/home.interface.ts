import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { INotificationItem } from '../../core/interfaces/notification-item.interface';


export interface HomeInterface extends IAbstractScreen {
    menuItems: IActionItem[];
    backgroundImage: string;
    logo: string;
    notificationItems: INotificationItem[];
}
