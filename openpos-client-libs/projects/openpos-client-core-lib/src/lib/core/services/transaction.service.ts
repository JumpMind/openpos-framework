import {Injectable} from '@angular/core';
import {TransactionStatusEnum} from '../../shared/transaction-status.enum';

@Injectable({
    providedIn: 'root',
})
export class TransactionService {
    mapStatusToCssClass(status: TransactionStatusEnum): string {
        switch(status) {
            case TransactionStatusEnum.InProgress:
            case TransactionStatusEnum.Completed:
                return 'success';
            case TransactionStatusEnum.Suspended:
            case TransactionStatusEnum.SuspendRetrieved:
            case TransactionStatusEnum.SuspendCancelled:
                return 'warn-light';
            case TransactionStatusEnum.Orphaned:
            case TransactionStatusEnum.Aborted:
            case TransactionStatusEnum.Cancelled:
            case TransactionStatusEnum.Failed:
            case TransactionStatusEnum.Voided:
                return 'warn';
        }
    }
}
