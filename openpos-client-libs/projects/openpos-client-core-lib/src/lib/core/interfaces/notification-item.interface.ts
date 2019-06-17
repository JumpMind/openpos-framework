import { NotificationType } from './notification-type.enum';

export interface INotificationItem {
    id: string;
    value: string;
    type: NotificationType;
}
