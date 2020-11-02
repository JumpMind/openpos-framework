import { IActionItem } from '../../../core/actions/action-item.interface';
import {TransStatusEnum} from '../../trans-status.enum';
import {TransTypeEnum} from '../../trans-type.enum';

export interface ITransactionSummary {
    sequenceNumber: string;
    sequenceNumberFormatted: string;
    voidedSequenceNumber: string;
    voidedSequenceNumberFormatted: string;
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
    status: TransStatusEnum;
    statusText: string;
    statusIcon: string;
    username: string;
    actions: IActionItem[];
    labels: any;
    transactionTypeText: string;
    transactionType: TransTypeEnum;
    transactionTypeIcon: string;
    tenderTypeIcons: string[];
}
