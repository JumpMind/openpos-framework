import { FullPageImageComponent } from './../screens/full-page-image/full-page-image.component';
import { TillCountOtherTenderComponent } from './../screens/till/till-count-other-tender.component';
import { OptionsComponent } from './../screens/options/options.component';
import { BlankComponent } from '../templates/blank/blank.component';
import { BlankWithBarComponent } from '../templates/blank-with-bar/blank-with-bar.component';
import { LoginComponent } from './../screens/login/login.component';
import { PersonalizationComponent } from '../screens/personalization.component';
import { WarrantyCoverageComponent } from '../screens/warranty-coverage.component';
import { SaleRetrievalComponent } from '../screens/sale-retrieval.component';
import { BasicItemSearchComponent } from '../screens/basic-item-search.component';
import { EmbeddedWebPageComponent } from '../screens/embedded-web-page.component';
import { PromptComponent } from '../screens/prompt.component';
import { TransactionComponent } from '../screens/transaction/transaction.component';
import { SelfCheckoutTransactionComponent } from '../self-checkout/self-checkout-transaction/self-checkout-transaction.component';
import { SellItemDetailComponent } from '../screens/sell-item-detail.component';
import { SignatureCaptureComponent } from '../screens/signature-capture.component';
import { PaymentStatusComponent } from '../screens/payment-status.component';
import { SelfCheckoutPaymentStatusComponent } from '../self-checkout/self-checkout-payment-status/self-checkout-payment-status.component';
import { FormComponent } from '../screens/form.component';
import { DynamicFormComponent } from '../screens/dynamic-form/dynamic-form.component';
import { HomeComponent } from '../screens/home.component';
import { SelfCheckoutHomeComponent } from '../self-checkout/self-checkout-home/self-checkout-home.component';
import { ItemListComponent } from '../screens/item-list.component';
import { ChooseOptionsComponent } from '../screens/choose-options/choose-options.component';
import { PromptWithOptionsComponent } from '../screens/prompt-with-options.component';
import { PromptWithInfoComponent } from '../screens/prompt-with-info.component';
import { StaticTableComponent } from '../screens/static-table.component';
import { IScreen } from '../common/iscreen';
import { IDialog } from '../common/idialog';
import { Observable } from 'rxjs/Observable';
import { Message } from '@stomp/stompjs';
import { Subscription } from 'rxjs/Subscription';
import { Injectable, Type, ComponentFactoryResolver, ComponentFactory } from '@angular/core';
import { StompService, StompState } from '@stomp/ng2-stompjs';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { TenderingComponent } from '../screens/tendering.component';
import { SellComponent } from '../templates/sell/sell.component';
import { SelfCheckoutWithBarComponent } from '../templates/selfcheckout-with-bar/selfcheckout-with-bar.component';
import { TillSummaryComponent } from '../screens/till/till-summary.component';
import { TillCountComponent } from '../screens/till/till-count.component';
import { ChangeComponent } from '../screens/change/change.component';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { SessionService } from '../services/session.service';
import { PrintPreviewComponent } from '../screens/print-preview.component';
import { WaitComponent } from '../screens/wait/wait.component';
import { CustomerSearchResultsComponent } from '../screens/customer-search-results/customer-search-results.component';
import { SelfCheckoutOptionsComponent } from '../self-checkout/self-checkout-options/self-checkout-options.component';
import { CallForAuthorizationComponent } from '../screens/call-for-authorization/call-for-authorization.component'
import { DialogComponent } from '../screens/dialog/dialog.component';
import { MultipleDynamicFormComponent } from '../screens/multiple-dynamic-form/multiple-dynamic-form.component';

@Injectable()
export class ScreenService {

  private screens = new Map<string, Type<IScreen>>();

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private http: HttpClient,
    private sessionService: SessionService) {
    // To make a screen available add it here and in entryComponents in the app.module.ts
    this.screens.set('BasicItemSearch', BasicItemSearchComponent);
    this.screens.set('ChooseOptions', ChooseOptionsComponent);
    this.screens.set('EmbeddedWebPage', EmbeddedWebPageComponent);
    this.screens.set('Form', FormComponent);
    this.screens.set('DynamicForm', DynamicFormComponent);
    this.screens.set('Login', LoginComponent);
    this.screens.set('Home', HomeComponent);
    this.screens.set('SelfCheckoutHome', SelfCheckoutHomeComponent);
    this.screens.set('ItemList', ItemListComponent);
    this.screens.set('PaymentStatus', PaymentStatusComponent);
    this.screens.set('SelfCheckoutPaymentStatus', SelfCheckoutPaymentStatusComponent);
    this.screens.set('Prompt', PromptComponent);
    this.screens.set('PromptWithOptions', PromptWithOptionsComponent);
    this.screens.set('PromptWithInfo', PromptWithInfoComponent);
    this.screens.set('Transaction', TransactionComponent);
    this.screens.set('SelfCheckoutTransaction', SelfCheckoutTransactionComponent);
    this.screens.set('SellItemDetail', SellItemDetailComponent);
    this.screens.set('SignatureCapture', SignatureCaptureComponent);
    this.screens.set('Table', StaticTableComponent);
    this.screens.set('SaleRetrieval', SaleRetrievalComponent);
    this.screens.set('Tendering', TenderingComponent);
    this.screens.set('WarrantyCoverage', WarrantyCoverageComponent);
    this.screens.set('Personalization', PersonalizationComponent);
    this.screens.set('TillCurrencyCount', TillCountComponent);
    this.screens.set('TillOtherTenderCount', TillCountOtherTenderComponent);
    this.screens.set('TillSummary', TillSummaryComponent);
    this.screens.set('Options', OptionsComponent);
    this.screens.set('Change', ChangeComponent);
    this.screens.set('PrintPreview', PrintPreviewComponent);
    this.screens.set('Wait', WaitComponent);
    this.screens.set('CustomerSearch', CustomerSearchResultsComponent);
    this.screens.set('SelfCheckoutOptions', SelfCheckoutOptionsComponent);
    this.screens.set('FullPageImage', FullPageImageComponent);
    this.screens.set('CallForAuthorization', CallForAuthorizationComponent);
    this.screens.set('MultipleDynamicForm', MultipleDynamicFormComponent);

    // Default Dialog 
    this.screens.set('Dialog', DialogComponent);

    // Templates
    this.screens.set('Blank', BlankComponent);
    this.screens.set('BlankWithBar', BlankWithBarComponent);
    this.screens.set('Sell', SellComponent);
    this.screens.set('SelfCheckout', SelfCheckoutWithBarComponent);
  }

  public addScreen(name: string, type: Type<IScreen>): void {
    if (this.screens.get(name)) {
      // tslint:disable-next-line:max-line-length
      console.log(`replacing registration for screen of type ${this.screens.get(name).name} with ${type.name} for the key of ${name} in the screen service`);
      this.screens.delete(name);
    }
    this.screens.set(name, type);
  }

  public hasScreen(name: string): boolean {
    return this.screens.has(name);
  }

  public resolveScreen(type: string): ComponentFactory<IScreen> {
    const screenType: Type<IScreen> = this.screens.get(type);
    if (screenType) {
      return this.componentFactoryResolver.resolveComponentFactory(screenType);
    } else {
      return null;
    }
  }

  public getFieldValues(fieldId: string, searchTerm?: string): Observable<any> {
    const url: string = this.sessionService.getApiServerBaseURL() + '/app/'
      + this.sessionService.getAppId() + '/node/'
      + this.sessionService.getNodeId() + '/control/'
      + fieldId;

    const httpParams = {};
    if (searchTerm) {
      httpParams['searchTerm'] = searchTerm;
    }
    console.log(`Requesting field values from the server using url: ${url}, params: '${JSON.stringify(httpParams)}'`);
    return this.http.get(url, {params: httpParams});
  }

}


