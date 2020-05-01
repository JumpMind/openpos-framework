import { PromptInterface } from '../prompt/prompt.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { PromptPosition } from '../../shared/screen-parts/prompt-form-part/prompt-position.enum';

export interface PromptWithInfoInterface extends PromptInterface {
    promptPosition: PromptPosition;
    infoForm: IForm;
}
