import { FieldInputType } from '../../../core/interfaces/field-input-type.enum';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Validator } from '@angular/forms';
import { PromptPosition } from './prompt-position.enum';
import { IForm } from '../../../core/interfaces/form.interface';

export interface PromptFormPartInterface {
    type: string;
    promptIcon: string;
    placeholderText: string;
    hintText: string;
    instructions: string;
    responseText: string;
    editable: boolean;
    responseType: FieldInputType;
    actionButton: IActionItem;
    otherActions: IActionItem[];
    minLength: number;
    maxLength: number;
    max: number;
    min: number;
    validationPatterns: Array<string>;
    scanEnabled: boolean;
    keyboardPreference: string;
    validators: Validator[];
    validationMessages: Map<string, string>;
    promptPosition: PromptPosition;
    infoForm: IForm;
}
