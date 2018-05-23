import { IMenuItem } from './../../common/imenuitem';
import { IItem } from './../../common/iitem';
import { SessionService } from './../../services/session.service';
import { IScreen } from './../../common/iscreen';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import createNumberMask from 'text-mask-addons/dist/createNumberMask';
import { OnDestroy } from '@angular/core/src/metadata/lifecycle_hooks';

@Component({
  selector: 'app-till-count-other-tender',
  templateUrl: './till-count-other-tender.component.html',
  styleUrls: ['./till-count-other-tender.component.scss']
})
export class TillCountOtherTenderComponent implements OnInit, OnDestroy, IScreen {

  @ViewChild('amountInput') amountField: ElementRef;

  amountValue = undefined;
  totalAmount: number;
  nextAction: IMenuItem;
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

  constructor(public session: SessionService) {
  }

  show(screen: any) {
    this.screen = screen;
    this.items = screen.items;
    this.totalAmount = 0;
    if (screen.total) {
      this.totalAmount = Number(screen.total);
    }
  }

  ngOnDestroy(): void {
  }

  ngOnInit(): void {
    this.nextAction = this.screen.nextAction;
  }

  public doMenuItemAction(menuItem: IMenuItem, payLoad: any) {
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
        selected: false
      });

      this.totalAmount += Number(this.amountValue);
    }
    this.amountValue = '0.00';
  }

  onNextAction() {
    this.session.response = {items: this.items, total: this.totalAmount.toString()};
    this.session.onAction(this.nextAction.action);
  }


}
