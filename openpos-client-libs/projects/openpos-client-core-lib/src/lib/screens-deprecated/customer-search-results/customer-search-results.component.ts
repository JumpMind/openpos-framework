import { Component, Input } from '@angular/core';
import { ICustomer } from './customer.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { MatSelectionListChange } from '@angular/material/list';

/**
 * @ignore
 */
@Component({
  selector: 'app-customer-search-results',
  templateUrl: './customer-search-results.component.html',
  styleUrls: ['./customer-search-results.component.scss']
})
export class CustomerSearchResultsComponent extends PosScreen<any> {

  @Input() submitAction: string;
  public customers: ICustomer[];
  selectedOptions: ICustomer[];
  localMenuItems: any;

  buildScreen() {
    this.customers = this.screen.customers;
    this.submitAction = this.screen.submitAction;
    this.localMenuItems = this.screen.template.localMenuItems;

    if (this.localMenuItems) {
      this.localMenuItems.forEach(element => {
        this.session.registerActionPayload(element.action, () => this.selectedOptions);
        element.enabled = false;
      });
    }
  }

  onSubmitAction(): void {
    this.session.onAction(this.submitAction, this.selectedOptions);
  }

  isSelectedOptionsEmpty(): boolean {
    return Boolean(typeof this.selectedOptions === 'undefined' || this.selectedOptions.length === 0);
  }

  selectionChange(event: MatSelectionListChange): void {
    if (event.option.selected) {
      const customer: ICustomer = event.option.value;
      this.enableOptions(customer);
    } else if (this.isSelectedOptionsEmpty()) {
      this.localMenuItems.forEach(element => {
        this.enableEditForUnselect(element);
      });
    }
  }

  private enableOptions(customer: ICustomer) {
    if (this.localMenuItems) {
      this.localMenuItems.forEach(element => {
        this.enableMenuItem(element, customer);
      });
    }
  }

  private enableMenuItem(element: any, customer: ICustomer) {
    element.enabled = false;
    if (customer.loyaltyId && customer.loyaltyId.length > 0) {
      if (element.action === 'Edit') {
        element.enabled = true;
      }
    } else {
      if (element.action === 'Enroll') {
        element.enabled = true;
      }
    }
  }

  private enableEditForUnselect(element: any) {
    if (element.action === 'Edit') {
      element.enabled = false;
    }
  }

}
