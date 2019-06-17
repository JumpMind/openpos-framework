import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { SelfCheckoutOptionsPartInterface } from '../screen-parts/self-checkout-options-part/self-checkout-options-part.interface';


export interface SelfCheckoutOptionsInterface extends IAbstractScreen {
    title: string;
    prompt: string;
    icon: string;
    imageUrl: string;
    selfCheckoutOptionsPart: SelfCheckoutOptionsPartInterface;
}
