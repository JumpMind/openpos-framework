import { IMaskSpec } from '../../shared/textmask';
import {IActionItem} from '../actions/action-item.interface';
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
    valueChangedAction: IActionItem;
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
    startAtDate: any;
    hintText: string;
    hideButtons: boolean;
    readOnly: boolean;
    preValidate: boolean;
}

export interface IDynamicListField {
    dynamicListEnabled: boolean;
}

export type LabelPositionType = 'before' | 'after';
export interface ICheckboxField extends IFormElement {
    labelPosition: LabelPositionType;
    checked: boolean;
}

export interface IPopTartField extends IFormElement {
    instructions: string;
    searchable: boolean;
}