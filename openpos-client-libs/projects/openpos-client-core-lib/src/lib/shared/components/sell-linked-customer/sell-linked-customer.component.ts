import { Component, Input } from '@angular/core';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';

/**
 * @ignore
 */
@Component({
    selector: 'app-sell-linked-customer',
    templateUrl: './sell-linked-customer.component.html',
    styleUrls: ['./sell-linked-customer.component.scss']
})

export class SellLinkedCustomerComponent {
    @Input() screenType: string;
    @Input() customerName: string;
    @Input() noCustomerText: string;
    @Input() loyaltyButton?: IActionItem;

    constructor(public actionService: ActionService) { }

    onClick() {
        if (this.loyaltyButton) {
            this.actionService.doAction(this.loyaltyButton);
        }
    }
}
