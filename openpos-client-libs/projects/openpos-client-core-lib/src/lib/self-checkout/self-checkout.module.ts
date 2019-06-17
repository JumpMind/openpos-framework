// Angular Includes
import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { ScreenConstants } from './screen.constants';
import { SelfCheckoutWithBarComponent } from './self-checkout-with-bar/self-checkout-with-bar.component';
import { SelfCheckoutTransactionComponent } from './self-checkout-transaction/self-checkout-transaction.component';
import { SelfCheckoutStatusBarComponent } from './self-checkout-statusbar/self-checkout-statusbar.component';
import { SelfCheckoutHomeComponent } from './self-checkout-home/self-checkout-home.component';
import { SelfCheckoutOptionsComponent } from './self-checkout-options/self-checkout-options.component';
import { ScreenService } from '../core/services/screen.service';
import { DialogService } from '../core/services/dialog.service';
import { SelfCheckoutFormComponent } from './self-checkout-form/self-checkout-form.component';
import { SelfCheckoutPromptComponent } from './self-checkout-prompt/self-checkout-prompt.component';
import { SelfCheckoutMenuComponent } from './screen-parts/self-checkout-menu/self-checkout-menu.component';
import { SelfCheckoutTenderComponent } from './self-checkout-tender/self-checkout-tender.component';
import { SelfCheckoutOptionsPartComponent } from './screen-parts/self-checkout-options-part/self-checkout-options-part.component';


const screens = [
    SelfCheckoutTransactionComponent,
    SelfCheckoutHomeComponent,
    SelfCheckoutOptionsComponent,
    SelfCheckoutFormComponent,
    SelfCheckoutPromptComponent,
    SelfCheckoutTenderComponent
];

const dialogs = [
];

const templates = [
    SelfCheckoutWithBarComponent
];

const components = [
    SelfCheckoutStatusBarComponent
];

const screenParts = [
    SelfCheckoutMenuComponent,
    SelfCheckoutOptionsPartComponent
];

@NgModule({
    entryComponents: [
        ...screens,
        ...dialogs,
        ...templates,
    ],
    declarations: [
        ...screens,
        ...dialogs,
        ...templates,
        ...components,
        ...screenParts,
    ],
    imports: [
        SharedModule
    ],
    exports: [
        SelfCheckoutTransactionComponent,
        ...templates,
        ...components,
        ...screenParts,
    ],
    providers: [
    ]
})
export class SelfCheckoutModule {
    constructor(screenService: ScreenService, dialogService: DialogService) {
        ScreenConstants.screens.forEach((screen) => {
            screenService.addScreen(screen.name, screen.component);
        });

        ScreenConstants.dialogs.forEach((dialog) => {
            dialogService.addDialog(dialog.name, dialog.component);
        });

        ScreenConstants.templates.forEach((template) => {
            screenService.addScreen(template.name, template.component);
        });
    }

 }
