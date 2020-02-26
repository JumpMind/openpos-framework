import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { ITransactionSummary } from '../../shared/components/transaction-summary/transaction-summary.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface TransactionSearchInterface extends IAbstractScreen {
    transactions: ITransactionSummary[];
    searchButton: IActionItem;
    clearButton: IActionItem;
}
