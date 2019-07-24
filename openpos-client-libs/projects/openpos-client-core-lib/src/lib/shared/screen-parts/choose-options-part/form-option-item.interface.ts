import { IActionItem } from '../../../core/actions/action-item.interface';
import { IForm } from '../../../core/interfaces/form.interface';

export interface IFormOptionItem {
    form: IForm;
    icon: string;
    name: string;
    optionAction: IActionItem;
    submitAction: string;
    instructions: string;
}
