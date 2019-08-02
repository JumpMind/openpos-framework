import { ITransactionReceiptLine } from './transaction-receipt-line.interface';

export interface ITransactionReceipt {
    transactionNumber: number;
    webOrderId: string;
    transactionInfoSection: ITransactionReceiptLine[];
    totalsInfoSection: ITransactionReceiptLine[];
}
