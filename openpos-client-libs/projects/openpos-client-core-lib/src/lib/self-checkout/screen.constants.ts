
import { SelfCheckoutHomeComponent } from './self-checkout-home/self-checkout-home.component';
import { SelfCheckoutTransactionComponent } from './self-checkout-transaction/self-checkout-transaction.component';
import { SelfCheckoutWithBarComponent } from './self-checkout-with-bar/self-checkout-with-bar.component';

export const ScreenConstants = {
    screens : [
        { name: 'SelfCheckoutHome', component: SelfCheckoutHomeComponent },
        { name: 'SelfCheckoutTransaction', component: SelfCheckoutTransactionComponent },
    ],

    dialogs: [
        { name: 'SelfCheckoutHome', component: SelfCheckoutHomeComponent },
        { name: 'SelfCheckoutTransaction', component: SelfCheckoutTransactionComponent },
    ],

    templates: [
        { name: 'SelfCheckout', component: SelfCheckoutWithBarComponent }
    ]

};
