import { ISellScreen } from "../..";
import { IForm } from "../../common/iform";


export interface IMultipleDynamicFormScreen extends ISellScreen {
    forms: IMultipleFormOption[];
    submittedForm: IForm;
}

export interface IMultipleFormOption {
    form: IForm;
    icon: string;
    name: string;
    submitAction: string;
    instructions: string;
}