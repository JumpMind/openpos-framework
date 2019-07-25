import { NgModule } from '@angular/core';

import { SaleComponent } from '../screens-with-parts/sale/sale.component';
import { SharedModule } from '../shared/shared.module';
import { ItemListComponent } from './item-list/item-list.component';
import { SaleRetrievalComponent } from './sale-retrieval/sale-retrieval.component';
import { SellItemDetailComponent } from './sell-item-detail/sell-item-detail.component';
import { TenderingComponent } from './tendering/tendering.component';
import { DynamicFormDialogComponent } from './dynamic-form/dynamic-form-dialog.component';
import { LoadingDialogComponent } from './loading-dialog/loading-dialog.component';
import { SignatureCaptureComponent } from './signature-capture/signature-capture.component';
import { BasicItemSearchComponent } from './basic-item-search/basic-item-search.component';
import { DetailTextScreenComponent } from './detail-text/detail-text-screen.component';
import { DynamicFormComponent } from './dynamic-form/dynamic-form.component';
import { DynamicListComponent } from './dynamic-list/dynamic-list.component';
import { EmbeddedWebPageComponent } from './embedded-web-page/embedded-web-page.component';
import { MultiselectItemListComponent } from './multiselect-item-list/multiselect-item-list.component';
import { PaymentStatusComponent } from './payment-status/payment-status.component';
import { PromptWithInfoComponent } from './prompt-with-info/prompt-with-info.component';
import { VersionComponent } from './version/version.component';
import { OptionsComponent } from './options/options.component';

const screens = [
        ItemListComponent,
        SaleRetrievalComponent,
        SellItemDetailComponent,
        TenderingComponent,
        SaleComponent,
        SignatureCaptureComponent,
        BasicItemSearchComponent,
        DetailTextScreenComponent,
        DynamicFormComponent,
        DynamicListComponent,
        EmbeddedWebPageComponent,
        MultiselectItemListComponent,
        PaymentStatusComponent,
        PromptWithInfoComponent,
        OptionsComponent
    ];

const dialogs = [
        DynamicFormDialogComponent,
        LoadingDialogComponent,
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
