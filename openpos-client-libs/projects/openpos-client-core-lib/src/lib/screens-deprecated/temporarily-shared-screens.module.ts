import { NgModule } from '@angular/core';

import { SaleComponent } from '../screens-with-parts/sale/sale.component';
import { SharedModule } from '../shared/shared.module';
import { ItemListComponent } from './item-list/item-list.component';
import { SaleRetrievalComponent } from './sale-retrieval/sale-retrieval.component';
import { SellItemDetailComponent } from './sell-item-detail/sell-item-detail.component';
import { BlankComponent } from './templates/blank/blank.component';
import { BlankWithBarComponent } from './templates/blank-with-bar/blank-with-bar.component';
import { SellComponent } from './templates/sell-template/sell/sell.component';
import { TenderingComponent } from './tendering/tendering.component';
import { ItemSearchResultsComponent } from './item-search-results/item-search-results.component';
import { DynamicFormDialogComponent } from './dynamic-form/dynamic-form-dialog.component';
import { LoadingDialogComponent } from './loading-dialog/loading-dialog.component';
import { SignatureCaptureComponent } from './signature-capture/signature-capture.component';
import { MultipleDynamicFormDialogComponent } from './multiple-dynamic-form/multiple-dynamic-form-dialog.component';
import { BasicItemSearchComponent } from './basic-item-search/basic-item-search.component';
import { ChooseOptionsComponent } from './choose-options/choose-options.component';
import { DetailTextScreenComponent } from './detail-text/detail-text-screen.component';
import { DynamicFormComponent } from './dynamic-form/dynamic-form.component';
import { DynamicListComponent } from './dynamic-list/dynamic-list.component';
import { EmbeddedWebPageComponent } from './embedded-web-page/embedded-web-page.component';
import { MultiselectItemListComponent } from './multiselect-item-list/multiselect-item-list.component';
import { PaymentStatusComponent } from './payment-status/payment-status.component';
import { PromptWithInfoComponent } from './prompt-with-info/prompt-with-info.component';
import { PromptWithOptionsComponent } from './prompt-with-options/prompt-with-options.component';
import { StaticTableComponent } from './static-table/static-table.component';
import { VersionComponent } from './version/version.component';
import { OptionsComponent } from './options/options.component';

const screens = [
        ItemListComponent,
        SaleRetrievalComponent,
        SellItemDetailComponent,
        BlankComponent,
        BlankWithBarComponent,
        SellComponent,
        TenderingComponent,
        SaleComponent,
        ItemSearchResultsComponent,
        SignatureCaptureComponent,
        BasicItemSearchComponent,
        ChooseOptionsComponent,
        DetailTextScreenComponent,
        DynamicFormComponent,
        DynamicListComponent,
        EmbeddedWebPageComponent,
        MultiselectItemListComponent,
        PaymentStatusComponent,
        PromptWithInfoComponent,
        PromptWithOptionsComponent,
        StaticTableComponent,
        OptionsComponent
    ];

const dialogs = [
        DynamicFormDialogComponent,
        LoadingDialogComponent,
        MultipleDynamicFormDialogComponent,
        VersionComponent
    ];
/**
 * @ignore
 */
@NgModule({
    entryComponents: [
        ...screens,
        ...dialogs
    ],
    declarations: [
        ...screens,
        ...dialogs
    ],
    imports: [
        SharedModule
    ],
    exports: [
        ...screens,
        ...dialogs
    ],
    providers: [
    ]
})
export class TemporarilySharedScreens {}
