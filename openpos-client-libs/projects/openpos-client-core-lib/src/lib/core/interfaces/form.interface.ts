import { IFormElement } from './form-field.interface';

export interface IForm {
    formElements: IFormElement[];
    requiresAtLeastOneValue: boolean;
    formErrors: string[];
    name: string;
  }
