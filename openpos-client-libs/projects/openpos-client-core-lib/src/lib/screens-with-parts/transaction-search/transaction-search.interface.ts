import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface TransactionSearchInterface extends IAbstractScreen {
    searchButton: IActionItem;
    clearButton: IActionItem;
    providerKey: string;
    noResultsMessage: string;
}
