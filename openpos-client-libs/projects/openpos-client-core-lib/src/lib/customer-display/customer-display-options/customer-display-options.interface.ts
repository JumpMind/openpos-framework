import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
// tslint:disable-next-line:max-line-length
import { SelfCheckoutOptionsPartInterface } from '../../self-checkout/screen-parts/self-checkout-options-part/self-checkout-options-part.interface';



export interface CustomerDisplayOptionsInterface extends IAbstractScreen {
    title: string;
    prompt: string;
    icon: string;
    imageUrl: string;
    selfCheckoutOptionsPart: SelfCheckoutOptionsPartInterface;
}
