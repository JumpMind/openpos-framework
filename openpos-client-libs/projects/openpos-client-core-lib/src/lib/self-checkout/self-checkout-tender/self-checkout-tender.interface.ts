import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { ITotal } from '../../core/interfaces/total.interface';
import { SelfCheckoutOptionsPartInterface } from '../screen-parts/self-checkout-options-part/self-checkout-options-part.interface';


export interface SelfCheckoutTenderInterface extends IAbstractScreen {
    title: string;
    prompt: string;
    amountDue: ITotal;
    amounts: ITotal[];
    imageUrl: string;
    selfCheckoutOptionsPart: SelfCheckoutOptionsPartInterface;
}
