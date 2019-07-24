import { Component, Input, HostListener } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';

@Component({
  selector: 'app-receipt-card',
  templateUrl: './receipt-card.component.html',
  styleUrls: ['./receipt-card.component.scss']
})
export class ReceiptCardComponent {

  @Input()
  public receipt: any;

  @Input()
  public removeReceiptAction: IActionItem;

  public hover = false;

  constructor(public actionService: ActionService) {
  }

  @HostListener('mouseenter')
  onMouseEnter() {
    this.hover = true;
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    this.hover = false;
  }

}
