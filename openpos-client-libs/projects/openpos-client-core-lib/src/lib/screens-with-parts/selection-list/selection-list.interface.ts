import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import {SelectableItemInterface} from './selectable-item.interface';

export interface SelectionListInterface<T extends SelectableItemInterface> extends IAbstractScreen {
    instructions: string;
    noListItemsPlaceholderText: string;
    noListItemsPlaceholderIcon: string;
    showScan: boolean;
    selectionButtons: IActionItem[];
    nonSelectionButtons: IActionItem[];
    selectionList: T[];
    multiSelect: boolean;
    defaultSelect: boolean;
    defaultSelectItemIndex: number;
    numberItemsPerPage: number;
    numberTotalItems: number;
    selectionChangedAction: string;
    fetchDataAction: string;
    sausageLinks: IActionItem[];
    allowNonSelectButtonWhenSelected: boolean;
}
