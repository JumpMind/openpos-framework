import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { OptionsListInterface } from '../../shared/screen-parts/options-list/options-list.interface';


export interface SelfCheckoutOptionsInterface extends IAbstractScreen {
    title: string;
    prompt: string;
    icon: string;
    imageUrl: string;
    optionsList: OptionsListInterface;
}
