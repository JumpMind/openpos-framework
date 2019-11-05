import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { FieldInputType } from '../../../core/interfaces/field-input-type.enum';

export interface SearchExpandInputInterface extends IAbstractScreen {
    scanMinLength: number;
    scanMaxLength: number;
    scanActionName: string;
    keyedActionName: string;
    searchText: string;
    inputType: FieldInputType;
    scanIcon: string;
}
