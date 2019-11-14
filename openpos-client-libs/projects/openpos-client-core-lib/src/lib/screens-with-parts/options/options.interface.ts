import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { OptionsListInterface } from '../../shared/screen-parts/options-list/options-list.interface';

export interface OptionsInterface extends IAbstractScreen {
    title: string;
    prompt: string;
    imageUrl: string;
    optionsList: OptionsListInterface;
}
