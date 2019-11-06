import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { ITotal } from '../../core/interfaces/total.interface';
import { OptionsListInterface } from '../../shared/screen-parts/options-list/options-list.interface';


export interface SelfCheckoutTenderInterface extends IAbstractScreen {
    title: string;
    prompt: string;
    amountDue: ITotal;
    amounts: ITotal[];
    imageUrl: string;
    selfCheckoutOptionsPart: OptionsListInterface;
}
