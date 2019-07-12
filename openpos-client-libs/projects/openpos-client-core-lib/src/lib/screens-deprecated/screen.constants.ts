import { MultiselectItemListComponent } from './multiselect-item-list/multiselect-item-list.component';
import { BasicItemSearchComponent } from './basic-item-search/basic-item-search.component';
import { ChooseOptionsComponent } from './choose-options/choose-options.component';
import { EmbeddedWebPageComponent } from './embedded-web-page/embedded-web-page.component';
import { FormComponent } from './form/form.component';
import { DynamicFormComponent } from './dynamic-form/dynamic-form.component';
import { DynamicListComponent } from './dynamic-list/dynamic-list.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ItemListComponent } from './item-list/item-list.component';
import { PaymentStatusComponent } from './payment-status/payment-status.component';
import { PromptComponent } from './prompt/prompt.component';
import { PromptWithOptionsComponent } from './prompt-with-options/prompt-with-options.component';
import { PromptWithInfoComponent } from './prompt-with-info/prompt-with-info.component';
import { TransactionComponent } from './transaction/transaction.component';
import { SellItemDetailComponent } from './sell-item-detail/sell-item-detail.component';
import { SignatureCaptureComponent } from './signature-capture/signature-capture.component';
import { StaticTableComponent } from './static-table/static-table.component';
import { TenderingComponent } from './tendering/tendering.component';
import { OptionsComponent } from './options/options.component';
import { ChangeComponent } from './change/change.component';
import { PrintPreviewComponent } from './print-preview/print-preview.component';
import { WaitComponent } from './wait/wait.component';
import { CustomerSearchResultsComponent } from './customer-search-results/customer-search-results.component';
import { FullPageImageComponent } from './full-page-image/full-page-image.component';
import { CallForAuthorizationComponent } from './call-for-authorization/call-for-authorization.component';
import { MultipleDynamicFormComponent } from './multiple-dynamic-form/multiple-dynamic-form.component';
import { SelectionListComponent } from './selection-list/selection-list.component';
import { ItemSearchResultsComponent } from './item-search-results/item-search-results.component';
import { ItemOptionsComponent } from './item-options/item-options.component';
import { DefaultDialogComponent } from './dialog/default-dialog.component';
import { BlankComponent } from './templates/blank/blank.component';
import { BlankWithBarComponent } from './templates/blank-with-bar/blank-with-bar.component';
import { SellComponent } from './templates/sell-template/sell/sell.component';
import { ChooseOptionsDialogComponent } from './choose-options/choose-options-dialog.component';
import { DynamicFormDialogComponent } from './dynamic-form/dynamic-form-dialog.component';
import { MultipleDynamicFormDialogComponent } from './multiple-dynamic-form/multiple-dynamic-form-dialog.component';
import { DynamicListDialogComponent } from './dynamic-list/dynamic-list-dialog.component';
import { LoginDialogComponent } from './login/login-dialog.component';
import { VersionComponent } from './version/version.component';
import { SaleRetrievalComponent } from './sale-retrieval/sale-retrieval.component';
import { TillCountComponent } from './till-count/till-count.component';
import { TillCountOtherTenderComponent } from './till-count-other-tender/till-count-other-tender.component';
import { TillSummaryComponent } from './till-summary/till-summary.component';
import { CatalogBrowserComponent } from './catalog-browser/catalog-browser.component';
import { DetailTextScreenComponent } from './detail-text/detail-text-screen.component';
import { PromptDialogComponent } from './prompt/prompt-dialog.component';
import { SaleComponent } from '../screens-with-parts/sale/sale.component';
import { PromptWithOptionsDialogComponent } from './prompt-with-options/prompt-with-options-dialog.component';
import { SelectionListDialogComponent } from './selection-list/selection-list-dialog.component';
import { DataTableComponent } from './data-table/data-table.component';
import { AutoCompleteAddressComponent } from './auto-complete-address/auto-complete-address.component';

/**
 * @ignore
 */
export const ScreenConstants = {
    screens : [
        { name: 'BasicItemSearch', component: BasicItemSearchComponent },
        { name: 'ChooseOptions', component: ChooseOptionsComponent },
        { name: 'EmbeddedWebPage', component: EmbeddedWebPageComponent },
        { name: 'Form', component: FormComponent },
        { name: 'DynamicForm', component: DynamicFormComponent },
        { name: 'DynamicList', component: DynamicListComponent },
        { name: 'Login', component: LoginComponent },
        { name: 'Home', component: HomeComponent },
        { name: 'ItemList', component: ItemListComponent },
        { name: 'MultiselectItemList', component: MultiselectItemListComponent },
        { name: 'PaymentStatus', component: PaymentStatusComponent },
        { name: 'Prompt', component: PromptComponent },
        { name: 'PromptWithOptions', component: PromptWithOptionsComponent },
        { name: 'PromptWithInfo', component: PromptWithInfoComponent },
        { name: 'Transaction', component: TransactionComponent },
        { name: 'SellItemDetail', component: SellItemDetailComponent },
        { name: 'SignatureCapture', component: SignatureCaptureComponent },
        { name: 'Table', component: StaticTableComponent },
        { name: 'SaleRetrieval', component: SaleRetrievalComponent },
        { name: 'Tendering', component: TenderingComponent },
        { name: 'TillCurrencyCount', component: TillCountComponent },
        { name: 'TillOtherTenderCount', component: TillCountOtherTenderComponent },
        { name: 'TillSummary', component: TillSummaryComponent },
        { name: 'Options', component: OptionsComponent },
        { name: 'Change', component: ChangeComponent },
        { name: 'PrintPreview', component: PrintPreviewComponent },
        { name: 'Wait', component: WaitComponent },
        { name: 'CustomerSearch', component: CustomerSearchResultsComponent },
        { name: 'FullPageImage', component: FullPageImageComponent },
        { name: 'CallForAuthorization', component: CallForAuthorizationComponent },
        { name: 'MultipleDynamicForm', component: MultipleDynamicFormComponent },
        { name: 'SelectionList', component: SelectionListComponent },
        { name: 'CatalogBrowser', component: CatalogBrowserComponent},
        { name: 'ItemSearchResults', component: ItemSearchResultsComponent },
        { name: 'ItemOptions', component: ItemOptionsComponent },
        { name: 'DetailText', component: DetailTextScreenComponent },

        { name: 'Sale', component: SaleComponent },
        { name: 'DataTable', component: DataTableComponent },
        { name: 'AutoCompleteAddress', component: AutoCompleteAddressComponent},

        // Default Dialog
        { name: 'Dialog', component: DefaultDialogComponent },
    ],

    dialogs: [
        // To make a dialog screen available add it here and in entryComponents in the app.module.ts
        { name: 'ChooseOptions', component: ChooseOptionsDialogComponent },
        { name: 'DynamicForm', component: DynamicFormDialogComponent },
        { name: 'DynamicList', component: DynamicListDialogComponent },
        { name: 'MultipleDynamicForm', component: MultipleDynamicFormDialogComponent },
        { name: 'Dialog', component: DefaultDialogComponent },
        { name: 'Prompt', component: PromptDialogComponent },

        // Copied from the screen service. may eventually want to make dialog specific versions of these
        { name: 'BasicItemSearch', component: BasicItemSearchComponent },
        { name: 'EmbeddedWebPage', component: EmbeddedWebPageComponent },
        { name: 'Form', component: FormComponent },
        { name: 'Login', component: LoginComponent },
        { name: 'LoginDialog', component: LoginDialogComponent },
        { name: 'Home', component: HomeComponent },
        { name: 'ItemList', component: ItemListComponent },
        { name: 'MultiselectItemList', component: MultiselectItemListComponent },
        { name: 'PaymentStatus', component: PaymentStatusComponent },
        { name: 'PromptWithOptions', component: PromptWithOptionsDialogComponent },
        { name: 'PromptWithInfo', component: PromptWithInfoComponent },
        { name: 'Transaction', component: TransactionComponent },
        { name: 'SellItemDetail', component: SellItemDetailComponent },
        { name: 'SignatureCapture', component: SignatureCaptureComponent },
        { name: 'Table', component: StaticTableComponent },
        { name: 'SaleRetrieval', component: SaleRetrievalComponent },
        { name: 'Tendering', component: TenderingComponent },
        { name: 'TillCurrencyCount', component: TillCountComponent },
        { name: 'TillOtherTenderCount', component: TillCountOtherTenderComponent },
        { name: 'TillSummary', component: TillSummaryComponent },
        { name: 'Options', component: OptionsComponent },
        { name: 'Change', component: ChangeComponent },
        { name: 'PrintPreview', component: PrintPreviewComponent },
        { name: 'Wait', component: WaitComponent },
        { name: 'CustomerSearch', component: CustomerSearchResultsComponent },
        { name: 'FullPageImage', component: FullPageImageComponent },
        { name: 'CallForAuthorization', component: CallForAuthorizationComponent },
        { name: 'Version', component: VersionComponent },
        { name: 'SelectionListDialog', component: SelectionListDialogComponent },
        { name: 'ItemSearchResults', component: ItemSearchResultsComponent },
        { name: 'ItemOptions', component: ItemOptionsComponent },
        { name: 'Blank', component: BlankComponent },
        { name: 'DataTable', component: DataTableComponent },
        { name: 'AutoCompleteAddress', component: AutoCompleteAddressComponent},
    ],

    templates: [
        // Templates
        { name: 'Blank', component: BlankComponent },
        { name: 'BlankWithBar', component: BlankWithBarComponent },
        { name: 'Sell', component: SellComponent }
    ]

};
