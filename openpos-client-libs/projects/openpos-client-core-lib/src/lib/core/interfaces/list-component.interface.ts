import { IFormElement } from './form-field.interface';

export interface IListComponent {
    name : string;
    valueListHeader : string;
    valueListIconName : string;
    valueType : string;
    requiresAtLeastOneValue : boolean
    addingAllowed : boolean;
    removingAllowed : boolean;
    addValueField : IFormElement;
    summaryField : IFormElement;
    removedIndex : number;
    minValue : number;
    maxValue : number;
    valueList : string[];
    formErrors : string[];
}
