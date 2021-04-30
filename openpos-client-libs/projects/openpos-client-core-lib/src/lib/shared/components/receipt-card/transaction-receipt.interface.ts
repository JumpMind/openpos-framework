import { ITransactionReceiptLine } from './transaction-receipt-line.interface';
import { ITotal } from '../../../core/interfaces/total.interface';
import { ITenderItem } from '../../../core/interfaces/tender-item.interface';

export interface ITransactionReceipt {
    barcode: string;
    transactionNumber: number;
    webOrderId: string;
    transactionInfoSection: ITransactionReceiptLine[];
    totalsInfoSection: ITransactionReceiptLine[];
    transactionTotal: ITotal;
    icon: string;
    tenderInfoSection: ITenderItem[];
    tenderInfoSectionTitle: string;
}
