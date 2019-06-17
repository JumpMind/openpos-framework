import { IWorkStation } from './workstation.interface';
import { IScan } from './scan.interface';
import { IStatusBar } from './status-bar.interface';
import { IAbstractScreenTemplate } from '../../../../core/interfaces/abstract-screen-template.interface';
import { IActionItem } from '../../../../core/interfaces/action-item.interface';

/**
 * @ignore
 */
export interface ISellTemplate extends IAbstractScreenTemplate {
    scan: IScan;
    statusBar: IStatusBar;
    localMenuItems: IActionItem[];
    transactionMenuItems: IActionItem[];
    workstation: IWorkStation;
    operatorText: string;
    timestampBegin: number;
}

