import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { FieldInputType } from '../../../core/interfaces/field-input-type.enum';
import { ScanType } from '../scan-or-search/scan-type.enum';

export interface SearchExpandInputInterface extends IAbstractScreen {
    scanMinLength: number;
    scanMaxLength: number;
    scanActionName: string;
    keyedActionName: string;
    scanSomethingText: string;
    autoFocusOnScan: boolean;
    inputType: FieldInputType;
    scanType: ScanType;
    scanIcon: string;
}
