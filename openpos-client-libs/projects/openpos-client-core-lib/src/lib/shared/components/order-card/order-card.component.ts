import { Component, Input, HostListener, Optional, Output, EventEmitter } from '@angular/core';
import { IOrderSummary } from '../../../core/interfaces/order-summary.interface';
import { ActionService } from '../../../core/actions/action.service';
import { IActionItem } from '../../../core/actions/action-item.interface';

@Component({
    selector: 'app-order-card',
    templateUrl: './order-card.component.html',
    styleUrls: ['./order-card.component.scss']
})
export class OrderCardComponent {
    @Input() order: IOrderSummary;

    @Input()
    public removeOrderAction: IActionItem;

    @Output()
    removeButtonClick = new EventEmitter();

    public hover = false;

    constructor(@Optional() public actionService: ActionService) {
    }

    @HostListener('mouseenter')
    onMouseEnter() {
      this.hover = true;
    }

    @HostListener('mouseleave')
    onMouseLeave() {
      this.hover = false;
    }

    onRemoveAction() {
      if (this.actionService) {
        this.actionService.doAction(this.removeOrderAction, this.order.number);
      } else {
        this.removeButtonClick.emit(this.order);
      }
    }
}
