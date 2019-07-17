import { IForm } from '../../core/interfaces/form.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';


export interface IMultipleDynamicFormScreen extends IAbstractScreen {
    backButton: IActionItem;
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
