import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { ScreenService } from '../core/services/screen.service';
import { DialogService } from '../core/services/dialog.service';
import { CustomerDisplayHomeComponent } from './customer-display-home/customer-display-home.component';
import { CustomerDisplaySaleComponent } from './customer-display-sale/customer-display-sale.component';
import { CustomerDisplayOptionsComponent } from './customer-display-options/customer-display-options.component';
import { SelfCheckoutModule } from '../self-checkout/self-checkout.module';
import { CustomerDisplayPromptComponent } from './customer-display-prompt/customer-display-prompt.component';
import { CustomerDisplayFormComponent } from './customer-display-form/customer-display-form.component';

const screens = [
    CustomerDisplayHomeComponent,
    CustomerDisplaySaleComponent,
    CustomerDisplayOptionsComponent,
    CustomerDisplayPromptComponent,
    CustomerDisplayFormComponent
];

const dialogs = [
];

const components = [
];

const screenParts = [
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
        SharedModule,
        SelfCheckoutModule
    ],
    exports: [
        ...components,
        ...screenParts,
    ],
    providers: [
    ]
})
export class CustomerDisplayModule {
    constructor(screenService: ScreenService, dialogService: DialogService) {
    }
}
