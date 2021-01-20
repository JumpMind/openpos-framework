import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { TransactionSearchMode } from './transaction-search-mode.enum';

export interface TransactionSearchInterface extends IAbstractScreen {
    searchButton: IActionItem;
    clearButton: IActionItem;
    changeSearchModeButton: IActionItem;
    searchAllButton: IActionItem;
    transactionSearchMode: TransactionSearchMode;
    transSearchModeText: string;
    transSearchModeIcon: string;
    providerKey: string;
    noResultsMessage: string;
    filtersLabel: string;
    filters: IActionItem[];
}
