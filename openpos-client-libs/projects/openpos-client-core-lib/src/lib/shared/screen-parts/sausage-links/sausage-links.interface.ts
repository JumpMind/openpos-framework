import { INotificationItem } from '../../../core/interfaces/notification-item.interface';
import { IActionItem } from '../../../core/interfaces/action-item.interface';

export interface ISausageLinksInterface {
    links: IActionItem[];
    notificationItems?: INotificationItem[];
}
