import { IActionItem } from './action-item.interface';

export interface IUrlMenuItem extends IActionItem {
    url: string;
    targetMode: string;
    options: string;  // Corresponds to options/features that can be passed to window.open()
}
