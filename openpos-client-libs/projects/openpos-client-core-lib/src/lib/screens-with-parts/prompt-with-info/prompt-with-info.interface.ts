import { PromptInterface } from '../prompt/prompt.interface';
import { PromptPosition } from '../../shared/screen-parts/prompt-form-part/prompt-position.enum';
import { DisplayProperty } from '../../shared/components/display-property/display-property.interface';

export interface PromptWithInfoInterface extends PromptInterface {
    promptPosition: PromptPosition;
    info: DisplayProperty[];
}
