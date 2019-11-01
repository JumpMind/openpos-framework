import { ITransactionReceiptLine } from './transaction-receipt-line.interface';
import { ITotal } from '../../../core/interfaces/total.interface';

export interface ITransactionReceipt {
    transactionNumber: number;
    webOrderId: string;
    transactionInfoSection: ITransactionReceiptLine[];
    totalsInfoSection: ITransactionReceiptLine[];
    transactionTotal: ITotal;
    icon: string;
}
