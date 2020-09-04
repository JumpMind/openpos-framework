import { IActionItem } from '../../../core/actions/action-item.interface';

export interface ITransactionSummary {
    sequenceNumber: string;
    customerName: string;
    items: number;
    transactionDate: string;
    deviceId: string;
    storeId: string;
    tillId: string;
    barcode: string;
    total: string;
    icon: string;
    businessDate: string;
    status: string;
    username: string;
    actions: IActionItem[];
    labels: any;
    transactionType: string;
    tenderTypeIcons: string[];
}
