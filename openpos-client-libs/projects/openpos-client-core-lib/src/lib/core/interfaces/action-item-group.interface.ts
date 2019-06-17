import { IActionItem } from './action-item.interface';

export interface IActionItemGroup {
    title: string;
    keybind: string;
    actionItems: IActionItem[];
}
