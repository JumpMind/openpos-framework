import { Component, ViewChild, ElementRef } from '@angular/core';
import createNumberMask from 'text-mask-addons/dist/createNumberMask';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IItem } from '../../core/interfaces/item.interface';

/**
 * @ignore
 */
@Component({
  selector: 'app-till-count-other-tender',
  templateUrl: './till-count-other-tender.component.html',
  styleUrls: ['./till-count-other-tender.component.scss']
})
export class TillCountOtherTenderComponent extends PosScreen<any> {

  @ViewChild('amountInput') amountField: ElementRef;

  amountValue = undefined;
  totalAmount: number;
  nextAction: IActionItem;
  screen: any;

  items: IItem[];
  numberMask = createNumberMask({
    prefix: '',
    includeThousandsSeparator: false,
    allowDecimal: true,
    integerLimit: 9,
    requireDecimal: true,
    allowNegative: false
  });

  buildScreen() {
    this.items = this.screen.items;
    this.nextAction = this.screen.nextAction;
    this.totalAmount = 0;
    if (this.screen.total) {
      this.totalAmount = Number(this.screen.total);
    }
  }

  public doMenuItemAction(menuItem: IActionItem, payLoad: any) {
    if (menuItem.action === 'Remove') {
      this.totalAmount -= Number(this.items[payLoad].amount);
      this.items.splice(payLoad, 1);
    }
  }

  onEnterAmount(event: Event) {
    const amount = Number(this.amountValue);
    if (!isNaN(amount) && amount !== 0) {
      this.items.push({
        id: `amount${this.items.length}`,
        index: this.items.length,
        amount: this.amountValue,
        description: '',
        subtitle: '',
        fields: null,
        selected: false,
        enabled: true
      });

      this.totalAmount += Number(this.amountValue);
    }
    this.amountValue = '0.00';
  }

  onNextAction() {
    this.session.onAction(this.nextAction.action, {items: this.items, total: this.totalAmount.toString()});
  }


}
