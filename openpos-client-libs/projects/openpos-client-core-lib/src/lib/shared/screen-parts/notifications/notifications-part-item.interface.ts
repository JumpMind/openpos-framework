import {IActionItem} from '../../../core/actions/action-item.interface';

export interface INotificationsPartItem extends IActionItem {
    actionData: any;
    subItems: string[];
}
