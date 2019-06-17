import { IFormElement } from './form-field.interface';

export interface IForm {
    formElements: IFormElement[];
    requiresAtLeastOneValue: Boolean;
    formErrors: string[];
    name: string;
  }
