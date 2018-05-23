import { VersionComponent } from './../dialogs/version/version.component';
import { IScreen } from '../common/iscreen';
import { Injectable, Type, ComponentFactoryResolver, ComponentFactory } from '@angular/core';
import { SessionService } from '../services/session.service';
import { ChooseOptionsDialogComponent } from '../dialogs/choose-options/choose-options-dialog.component';
import { DynamicFormDialogComponent } from '../dialogs/dynamic-form/dynamic-form-dialog.component';
import { ScreenService } from '.';

import { MultipleDynamicFormDialogComponent } from '../dialogs/multiple-dynamic-form/multiple-dynamic-form-dialog.component';
import { BasicItemSearchComponent } from '../screens/basic-item-search.component';
import { EmbeddedWebPageComponent } from '../screens/embedded-web-page.component';
import { FormComponent } from '../screens/form.component';
import { DynamicFormComponent } from '../screens/dynamic-form/dynamic-form.component';
import { LoginComponent } from '../screens/login/login.component';
import { HomeComponent } from '../screens/home.component';
import { SelfCheckoutHomeComponent, SelfCheckoutTransactionComponent, SelfCheckoutOptionsComponent } from '../self-checkout';
import { ItemListComponent } from '../screens/item-list.component';
import { PaymentStatusComponent } from '../screens/payment-status.component';
import { SelfCheckoutPaymentStatusComponent } from '../self-checkout/self-checkout-payment-status/self-checkout-payment-status.component';
import { PromptComponent } from '../screens/prompt.component';
import { PromptWithOptionsComponent } from '../screens/prompt-with-options.component';
import { PromptWithInfoComponent } from '../screens/prompt-with-info.component';
import { TransactionComponent } from '../screens/transaction/transaction.component';
import { SellItemDetailComponent } from '../screens/sell-item-detail.component';
import { SignatureCaptureComponent } from '../screens/signature-capture.component';
import { StaticTableComponent } from '../screens/static-table.component';
import { SaleRetrievalComponent } from '../screens/sale-retrieval.component';
import { TenderingComponent } from '../screens/tendering.component';
import { WarrantyCoverageComponent } from '../screens/warranty-coverage.component';
import { PersonalizationComponent } from '../screens/personalization.component';
import { TillCountComponent } from '../screens/till/till-count.component';
import { TillCountOtherTenderComponent } from '../screens/till/till-count-other-tender.component';
import { TillSummaryComponent } from '../screens/till/till-summary.component';
import { OptionsComponent } from '../screens/options/options.component';
import { ChangeComponent } from '../screens/change/change.component';
import { PrintPreviewComponent } from '../screens/print-preview.component';
import { WaitComponent } from '../screens/wait/wait.component';
import { CustomerSearchResultsComponent } from '../screens/customer-search-results/customer-search-results.component';
import { FullPageImageComponent } from '../screens/full-page-image/full-page-image.component';
import { CallForAuthorizationComponent } from '../screens/call-for-authorization/call-for-authorization.component';
import { DialogComponent } from '../screens/dialog/dialog.component';
import { LoadingDialogComponent } from '../dialogs/loading-dialog/loading-dialog.component';
import { LoginDialogComponent } from '../dialogs/login-dialog/login-dialog.component';

@Injectable()
export class DialogService {

  private dialogs = new Map<string, Type<IScreen>>();

  constructor(private componentFactoryResolver: ComponentFactoryResolver,
    private sessionService: SessionService) {

    // To make a dialog screen available add it here and in entryComponents in the app.module.ts
    this.dialogs.set('ChooseOptions', ChooseOptionsDialogComponent);
    this.dialogs.set('DynamicForm', DynamicFormDialogComponent);
    this.dialogs.set('MultipleDynamicForm', MultipleDynamicFormDialogComponent);
    this.dialogs.set('Dialog', DialogComponent);
    this.dialogs.set('LoadingDialog', LoadingDialogComponent);

    // Copied from the screen service. may eventually want to make dialog specific versions of these
    this.dialogs.set('BasicItemSearch', BasicItemSearchComponent);
    this.dialogs.set('EmbeddedWebPage', EmbeddedWebPageComponent);
    this.dialogs.set('Form', FormComponent);
    this.dialogs.set('Login', LoginComponent);
    this.dialogs.set('LoginDialog', LoginDialogComponent);
    this.dialogs.set('Home', HomeComponent);
    this.dialogs.set('SelfCheckoutHome', SelfCheckoutHomeComponent);
    this.dialogs.set('ItemList', ItemListComponent);
    this.dialogs.set('PaymentStatus', PaymentStatusComponent);
    this.dialogs.set('SelfCheckoutPaymentStatus', SelfCheckoutPaymentStatusComponent);
    this.dialogs.set('Prompt', PromptComponent);
    this.dialogs.set('PromptWithOptions', PromptWithOptionsComponent);
    this.dialogs.set('PromptWithInfo', PromptWithInfoComponent);
    this.dialogs.set('Transaction', TransactionComponent);
    this.dialogs.set('SelfCheckoutTransaction', SelfCheckoutTransactionComponent);
    this.dialogs.set('SellItemDetail', SellItemDetailComponent);
    this.dialogs.set('SignatureCapture', SignatureCaptureComponent);
    this.dialogs.set('Table', StaticTableComponent);
    this.dialogs.set('SaleRetrieval', SaleRetrievalComponent);
    this.dialogs.set('Tendering', TenderingComponent);
    this.dialogs.set('WarrantyCoverage', WarrantyCoverageComponent);
    this.dialogs.set('Personalization', PersonalizationComponent);
    this.dialogs.set('TillCurrencyCount', TillCountComponent);
    this.dialogs.set('TillOtherTenderCount', TillCountOtherTenderComponent);
    this.dialogs.set('TillSummary', TillSummaryComponent);
    this.dialogs.set('Options', OptionsComponent);
    this.dialogs.set('Change', ChangeComponent);
    this.dialogs.set('PrintPreview', PrintPreviewComponent);
    this.dialogs.set('Wait', WaitComponent);
    this.dialogs.set('CustomerSearch', CustomerSearchResultsComponent);
    this.dialogs.set('SelfCheckoutOptions', SelfCheckoutOptionsComponent);
    this.dialogs.set('FullPageImage', FullPageImageComponent);
    this.dialogs.set('CallForAuthorization', CallForAuthorizationComponent);
    this.dialogs.set('Version', VersionComponent);
  }

  public addDialog(name: string, type: Type<IScreen>): void {
    if (this.dialogs.get(name)) {
      // tslint:disable-next-line:max-line-length
      console.log(`replacing registration for screen of type ${this.dialogs.get(name).name} with ${type.name} for the key of ${name} in the screen service`);
      this.dialogs.delete(name);
    }
    this.dialogs.set(name, type);
  }

  public hasDialog(name: string): boolean {
    return this.dialogs.has(name);
  }

  public resolveDialog(type: string): ComponentFactory<IScreen> {
    const dialogType: Type<IScreen> = this.dialogs.get(type);
    if (dialogType) {
        return this.componentFactoryResolver.resolveComponentFactory(dialogType);
    } else {
        return null;
    }
  }
}


