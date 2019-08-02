// Angular Includes
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';

// Screens
import { TransactionComponent } from './transaction/transaction.component';
import { FormComponent } from './form/form.component';
import { LoginComponent } from './login/login.component';
import { TillCountComponent } from './till-count/till-count.component';
import { TillCountOtherTenderComponent } from './till-count-other-tender/till-count-other-tender.component';
import { ChangeComponent } from './change/change.component';
import { WaitComponent } from './wait/wait.component';
import { CustomerSearchResultsComponent } from './customer-search-results/customer-search-results.component';
import { FullPageImageComponent } from './full-page-image/full-page-image.component';
import { CallForAuthorizationComponent } from './call-for-authorization/call-for-authorization.component';
import { MultipleDynamicFormComponent } from './multiple-dynamic-form/multiple-dynamic-form.component';
import { SelectionListComponent } from './selection-list/selection-list.component';
import { PrintPreviewComponent } from './print-preview/print-preview.component';
import { ItemOptionsComponent } from './item-options/item-options.component';
import { CatalogBrowserComponent } from './catalog-browser/catalog-browser.component';

// Templates
import { TillSummaryComponent } from './till-summary/till-summary.component';
import { DynamicListDialogComponent } from './dynamic-list/dynamic-list-dialog.component';
import { LoginDialogComponent } from './login/login-dialog.component';
import { ScreenConstants } from './screen.constants';
import { SelectionListDialogComponent } from './selection-list/selection-list-dialog.component';
import { DataTableComponent } from './data-table/data-table.component';
import { AutoCompleteAddressComponent } from './auto-complete-address/auto-complete-address.component';
import { TemporarilySharedScreens } from './temporarily-shared-screens.module';
import { DefaultDialogComponent } from './dialog/default-dialog.component';
import { ScreenService } from '../core/services/screen.service';
import { DialogService } from '../core/services/dialog.service';
import { IconService } from '../core/services/icon.service';
import { PromptDialogComponent } from './prompt/prompt-dialog.component';
import { PromptWithOptionsDialogComponent } from './prompt-with-options/prompt-with-options-dialog.component';
import { ChooseOptionsDialogComponent } from './choose-options/choose-options-dialog.component';
import { PromptComponent } from './prompt/prompt.component';
import { HomeComponent } from './home/home.component';

const screens = [
    FormComponent,
    LoginComponent,
    TransactionComponent,
    TillSummaryComponent,
    TillCountComponent,
    TillCountOtherTenderComponent,
    ChangeComponent,
    PrintPreviewComponent,
    CustomerSearchResultsComponent,
    FullPageImageComponent,
    CallForAuthorizationComponent,
    MultipleDynamicFormComponent,
    SelectionListComponent,
    CatalogBrowserComponent,
    ItemOptionsComponent,
    DataTableComponent,
    AutoCompleteAddressComponent,
    PromptComponent,
    HomeComponent
];

const dialogs = [
    DynamicListDialogComponent,
    LoginDialogComponent,
    SelectionListDialogComponent,
    DefaultDialogComponent,
    PromptDialogComponent,
    PromptWithOptionsDialogComponent,
    ChooseOptionsDialogComponent,
];

const templates = [
];

/**
 * @ignore
 */
@NgModule({
    entryComponents: [
        ...screens,
        ...dialogs,
        ...templates
    ],
    declarations: [
        ...screens,
        ...dialogs,
        ...templates,
    ],
    imports: [
        SharedModule,
        TemporarilySharedScreens
    ],
    exports: [
        ...templates,
    ],
    providers: [
    ]
})
export class ScreensModule {
    constructor(screenService: ScreenService, dialogService: DialogService, iconService: IconService) {
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
