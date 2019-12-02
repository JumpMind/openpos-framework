import { FieldInputType } from '../../../core/interfaces/field-input-type.enum';
import { ScanType } from './scan-type.enum';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface ScanOrSearchInterface {
    scanMinLength: number;
    scanMaxLength: number;
    scanAction: IActionItem;
    keyedAction: IActionItem;
    scanSomethingText: string;
    autoFocusOnScan: boolean;
    inputType: FieldInputType;
    scanType: ScanType;
    scanIcon: string;
}
