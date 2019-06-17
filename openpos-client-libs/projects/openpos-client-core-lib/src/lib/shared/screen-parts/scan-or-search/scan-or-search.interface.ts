import { FieldInputType } from '../../../core/interfaces/field-input-type.enum';
import { ScanType } from './scan-type.enum';

export interface ScanOrSearchInterface {
    scanMinLength: number;
    scanMaxLength: number;
    scanActionName: string;
    scanSomethingText: string;
    autoFocusOnScan: boolean;
    inputType: FieldInputType;
    scanType: ScanType;
    scanIcon: string;
}
