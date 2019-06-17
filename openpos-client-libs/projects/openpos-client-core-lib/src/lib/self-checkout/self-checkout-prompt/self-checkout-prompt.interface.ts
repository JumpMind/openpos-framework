import { IOptionItem } from '../../screens-deprecated/choose-options/option-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';

export interface SelfCheckoutPromptInterface extends IAbstractScreen {
    options: IOptionItem[];
    imageUrl: string;
}
