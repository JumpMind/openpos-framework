import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import {AutoCompleteAddressDialogComponent} from './auto-complete-address/auto-complete-address-dialog.component';

import { SelectionListScreenComponent } from './selection-list/selection-list-screen.component';
import { SelectionListScreenDialogComponent } from './selection-list/selection-list-screen-dialog.component';
import { GenericDialogComponent } from './dialog/generic-dialog.component';
import { PromptScreenComponent } from './prompt/prompt-screen.component';
import { PromptScreenDialogComponent } from './prompt/prompt-screen-dialog.component';
import { PromptWithOptionsScreenComponent } from './prompt-with-options/prompt-with-options-screen.component';
import { PromptWithOptionsScreenDialogComponent } from './prompt-with-options/prompt-with-options-screen-dialog.component';
import { HomeComponent } from './home/home.component';
import { ReturnComponent } from './return/return.component';
import { ReturnTransDetailsDialogComponent } from './return/return-trans-details/return-trans-details-dialog.component';
import { ChooseOptionsScreenDialogComponent } from './choose-options-dialog/choose-options-screen-dialog.component';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { LoadingDialogComponent } from './loading-dialog/loading-dialog.component';
import { PromptWithInfoScreenComponent } from './prompt-with-info/prompt-with-info-screen.component';
import { PromptWithInfoScreenDialogComponent } from './prompt-with-info/prompt-with-info-screen-dialog.component';
import { DataTableComponent } from './data-table/data-table.component';
import { DataTableDialogComponent } from './data-table/data-table-dialog.component';
import { AutoCompleteAddressComponent } from './auto-complete-address/auto-complete-address.component';
import { SaleComponent } from './sale/sale.component';
import { ItemDetailComponent } from './item-detail/item-detail.component';
import { TenderComponent } from './tender/tender.component';
import { SignatureCaptureComponent } from './signature-capture/signature-capture.component';
import { DynamicFormDialogComponent } from './dynamic-form-dialog/dynamic-form-dialog.component';
import { DynamicFormComponent } from './dynamic-form-dialog/dynamic-form.component';
import { ScanInputDialogComponent } from './scan-input-dialog/scan-input-dialog.component';
import { MobileReturnReceiptsSheetComponent } from './return/mobile-return-receipts-sheet/mobile-return-receipts-sheet.component';
import { OptionsScreenComponent } from './options/options-screen.component';
import { OptionsScreenDialogComponent } from './options/options-screen-dialog.component';
import { MobileSaleOrdersSheetComponent } from './sale/mobile-sale-orders-sheet/mobile-sale-orders-sheet.component';
import {ErrorDialogComponent} from './error-dialog/error-dialog.component';
import { TransactionSearchComponent } from './transaction-search/transaction-search.component';
import { TransactionDetailsComponent } from './transaction-details/transaction-details.component';
import {CustomerDetailsDialogComponent} from "./sale/customer-details-dialog/customer-details-dialog.component";
import {CustomerSearchResultDialogComponent} from './customer-search-result-dialog/customer-search-result-dialog.component';
import { LoyaltyCustomerFormDialogComponent } from "./loyalty-customer-form-dialog/loyalty-customer-form-dialog.component";
import {MembershipDetailsDialogComponent} from "./sale/membership-details-dialog/membership-details-dialog.component";


const screens = [
    SelectionListScreenComponent,
    PromptScreenComponent,
    PromptWithOptionsScreenComponent,
    PromptWithInfoScreenComponent,
    HomeComponent,
    ReturnComponent,
    MobileReturnReceiptsSheetComponent,
    DataTableComponent,
    AutoCompleteAddressComponent,
    SaleComponent,
    MobileSaleOrdersSheetComponent,
    ItemDetailComponent,
    TenderComponent,
    SignatureCaptureComponent,
    DynamicFormComponent,
    OptionsScreenComponent,
    OptionsScreenDialogComponent,
    TransactionSearchComponent,
    TransactionDetailsComponent
];

const dialogs = [
    SelectionListScreenDialogComponent,
    GenericDialogComponent,
    PromptScreenDialogComponent,
    PromptWithOptionsScreenDialogComponent,
    PromptWithInfoScreenDialogComponent,
    ChooseOptionsScreenDialogComponent,
    ReturnTransDetailsDialogComponent,
    ConfirmDialogComponent,
    LoadingDialogComponent,
    DynamicFormDialogComponent,
    ScanInputDialogComponent,
    CustomerDetailsDialogComponent,
    MembershipDetailsDialogComponent,
    AutoCompleteAddressDialogComponent,
    ErrorDialogComponent,
    DataTableDialogComponent,
    CustomerSearchResultDialogComponent,
    DataTableDialogComponent,
    LoyaltyCustomerFormDialogComponent
];

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
    ],
    providers: [
    ]
})
export class ScreensWithPartsModule {
}
