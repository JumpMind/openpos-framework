import { PromptInterface } from '../prompt/prompt.interface';
import { PromptPosition } from './prompt-position.enum';
import { IForm } from '../../core/interfaces/form.interface';

export interface PromptWithInfoInterface extends PromptInterface {
    promptPosition: PromptPosition;
    form: IForm;
}
