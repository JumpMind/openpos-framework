import { INotificationsPartItem } from './notifications-part-item.interface';

export interface NotificationsInterface {
    title: string;
    icon: string;
    items: INotificationsPartItem[];
}