import { Component, Input } from '@angular/core';
import { IActionItem } from '../../../../core/interfaces/action-item.interface';
import { SessionService } from '../../../../core/services/session.service';

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

    constructor(public session: SessionService) { }

    onClick() {
        if (this.loyaltyButton) {
            this.session.onAction(this.loyaltyButton);
        }
    }
}
