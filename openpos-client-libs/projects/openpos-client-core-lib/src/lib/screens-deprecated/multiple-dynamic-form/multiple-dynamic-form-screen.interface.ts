import { ISellScreen } from '../templates/sell-template/sell/sell-screen.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';


export interface IMultipleDynamicFormScreen extends ISellScreen {
    forms: IMultipleFormOption[];
    submittedForm: IForm;
}

export interface IMultipleFormOption {
    form: IForm;
    icon: string;
    name: string;
    submitButton: IActionItem;
    submitAction: string;
    instructions: string;
}
