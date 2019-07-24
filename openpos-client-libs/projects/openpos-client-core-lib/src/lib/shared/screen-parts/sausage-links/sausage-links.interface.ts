import { INotificationItem } from '../../../core/interfaces/notification-item.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface ISausageLinksInterface {
    links: IActionItem[];
    notificationItems?: INotificationItem[];
}
