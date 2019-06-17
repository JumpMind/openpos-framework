import { Component, Input, HostListener } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/interfaces/action-item.interface';

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

  constructor(public session: SessionService) {
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
