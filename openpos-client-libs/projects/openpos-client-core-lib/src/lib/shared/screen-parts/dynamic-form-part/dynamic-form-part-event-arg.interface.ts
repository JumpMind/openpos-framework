import {IForm} from '../../../core/interfaces/form.interface';
import {FormGroup} from '@angular/forms';

export interface IDynamicFormPartEventArg {
    form: IForm;
    formGroup: FormGroup;
}