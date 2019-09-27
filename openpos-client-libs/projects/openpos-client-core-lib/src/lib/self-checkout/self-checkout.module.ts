// Angular Includes
import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { SelfCheckoutHomeComponent } from './self-checkout-home/self-checkout-home.component';
import { SelfCheckoutOptionsComponent } from './self-checkout-options/self-checkout-options.component';
import { ScreenService } from '../core/services/screen.service';
import { DialogService } from '../core/services/dialog.service';
import { SelfCheckoutFormComponent } from './self-checkout-form/self-checkout-form.component';
import { SelfCheckoutPromptComponent } from './self-checkout-prompt/self-checkout-prompt.component';
import { SelfCheckoutMenuComponent } from './screen-parts/self-checkout-menu/self-checkout-menu.component';
import { SelfCheckoutTenderComponent } from './self-checkout-tender/self-checkout-tender.component';
import { SelfCheckoutOptionsPartComponent } from './screen-parts/self-checkout-options-part/self-checkout-options-part.component';
import { SelfCheckoutSaleComponent } from './self-checkout-sale/self-checkout-sale.component';
import { SelfCheckoutPromptPartComponent } from './screen-parts/self-checkout-prompt-part/self-checkout-prompt-part.component';


const screens = [
    SelfCheckoutHomeComponent,
    SelfCheckoutOptionsComponent,
    SelfCheckoutFormComponent,
    SelfCheckoutPromptComponent,
    SelfCheckoutTenderComponent,
    SelfCheckoutSaleComponent
];

const dialogs = [
];

const components = [
];

const screenParts = [
    SelfCheckoutMenuComponent,
    SelfCheckoutOptionsPartComponent,
    SelfCheckoutPromptPartComponent
];

@NgModule({
    entryComponents: [
        ...screens,
        ...dialogs,
    ],
    declarations: [
        ...screens,
        ...dialogs,
        ...components,
        ...screenParts,
    ],
    imports: [
        SharedModule
    ],
    exports: [
        ...components,
        ...screenParts,
    ],
    providers: [
    ]
})
export class SelfCheckoutModule {
    constructor(screenService: ScreenService, dialogService: DialogService) {
    }

}
