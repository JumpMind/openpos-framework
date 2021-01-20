import {Injectable} from '@angular/core';
import {TransStatusEnum} from '../../shared/trans-status.enum';
import {TransTypeEnum} from '../../shared/trans-type.enum';

@Injectable({
    providedIn: 'root',
})
export class TransactionService {
    mapStatusToCssClass(status: TransStatusEnum): string {
        switch(status) {
            case TransStatusEnum.InProgress:
            case TransStatusEnum.Completed:
                return 'success';
            case TransStatusEnum.Suspended:
            case TransStatusEnum.SuspendRetrieved:
            case TransStatusEnum.SuspendCancelled:
                return 'warn-light';
            case TransStatusEnum.Orphaned:
            case TransStatusEnum.Aborted:
            case TransStatusEnum.Cancelled:
            case TransStatusEnum.Failed:
            case TransStatusEnum.Voided:
                return 'warn';
        }
    }

    isRetailTrans(transType: TransTypeEnum): boolean {
        return transType === TransTypeEnum.Sale || transType === TransTypeEnum.Return;
    }
}
