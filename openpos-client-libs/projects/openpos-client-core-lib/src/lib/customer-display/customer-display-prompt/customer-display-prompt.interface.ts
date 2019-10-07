import { IOptionItem } from '../../core/interfaces/option-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';

export interface CustomerDisplayPromptInterface extends IAbstractScreen {
    options: IOptionItem[];
    imageUrl: string;
}
