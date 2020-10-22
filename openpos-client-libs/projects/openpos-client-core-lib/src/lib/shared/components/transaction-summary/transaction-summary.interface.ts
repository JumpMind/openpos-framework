import { IActionItem } from '../../../core/actions/action-item.interface';
import {TransactionStatusEnum} from '../../transaction-status.enum';

export interface ITransactionSummary {
    sequenceNumber: string;
    sequenceNumberFormatted: string;
    customerName: string;
    items: number;
    itemsFormatted: string;
    transactionDate: string;
    deviceId: string;
    storeId: string;
    tillId: string;
    barcode: string;
    total: string;
    businessDate: string;
    status: TransactionStatusEnum;
    statusText: string;
    statusIcon: string;
    username: string;
    actions: IActionItem[];
    labels: any;
    transactionType: string;
    transactionTypeIcon: string;
    tenderTypeIcons: string[];
}
