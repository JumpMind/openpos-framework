import { IMaskSpec } from '../../shared/textmask';
import { IConfirmationDialog } from '../actions/confirmation-dialog.interface';
import { ValidatorFn } from '@angular/forms';
export interface IFormElement {
    elementType: string;
    inputType: string;
    label: string;
    id: string;
    value: string;
    values: string[];
    placeholder: string;
    action: string;
    submitButton: boolean;
    required: boolean;
    selectedIndex: number;
    selectedIndexes: number[];
    valueChangedAction: string;
    mask: IMaskSpec;
    pattern: string;
    minValue: number;
    maxValue: number;
    minLength: number;
    maxLength: number;
    iconName: string;
    disabled: boolean;
    select: boolean;
    checked: boolean;
    keyboardPreference: string;
    confirmationDialog: IConfirmationDialog;
    confirmationMessage: string;
    scanEnabled: boolean;
    validators?: string[];
    additionalValidators?: ValidatorFn[];
    validationMessages: Map<string, string>;
    icon: string;
    valueDisplayMode: any;
    tabindex: any;
    labelPosition: any;
    mode: any;
    formatter: any;
    hideCalendar: any;
    maxDate: any;
    minDate: any;
    hintText: string;
    hideButtons: boolean;
}

export interface IDynamicListField {
    dynamicListEnabled: boolean;
}

export type LabelPositionType = 'before' | 'after';
export interface ICheckboxField extends IFormElement {
    labelPosition: LabelPositionType;
    checked: boolean;
}

export interface ISearchablePopTartField extends IFormElement {
    instructions: string;
}

