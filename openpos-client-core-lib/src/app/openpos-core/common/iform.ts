import { IFormElement } from "./iformfield";

export interface IForm {
    formElements: IFormElement[];
    requiresAtLeastOneValue: Boolean;
    formErrors: string[];
  }