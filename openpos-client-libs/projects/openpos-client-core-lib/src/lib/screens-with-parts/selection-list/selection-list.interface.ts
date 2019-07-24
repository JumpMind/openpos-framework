import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { ISelectionListItem } from './selection-list-item.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface SelectionListInterface extends IAbstractScreen {
    instructions: string;
    noListItemsPlaceholderText: string;
    noListItemsPlaceholderIcon: string;
    showScan: boolean;
    selectionButtons: IActionItem[];
    nonSelectionButtons: IActionItem[];
    selectionList: ISelectionListItem[];
    multiSelect: boolean;
    defaultSelect: boolean;
    defaultSelectItemIndex: number;
    numberItemsPerPage: number;
    numberTotalItems: number;
    selectionChangedAction: string;
    fetchDataAction: string;
    sausageLinks: IActionItem[];
}
