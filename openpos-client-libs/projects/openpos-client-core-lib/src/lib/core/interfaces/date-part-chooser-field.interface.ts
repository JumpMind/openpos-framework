import { IFormElement } from './form-field.interface';

export enum DatePartChooserMode {
    MonthDate = 'MonthDate',
    MonthYear = 'MonthYear',
    MonthDateYear = 'MonthDateYear'
}

export interface IDateParts {
    month: number;
    dayOfMonth: number;
    year: number;
}

export interface IDatePartChooserField extends IFormElement {
    month: number;
    dayOfMonth: number;
    year: number;
    mode: DatePartChooserMode;
    popupTitle?: string;
}

