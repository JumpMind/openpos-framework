import { SessionService } from './../../../core/services/session.service';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { Component, Input } from '@angular/core';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

@Component({
    selector: 'app-sell-item-line',
    templateUrl: './sell-item-line.component.html',
    styleUrls: ['./sell-item-line.component.scss']
})
export class SellItemLineComponent {

    @Input() item: ISellItem;
    @Input() readOnly: boolean;
    @Input() hideButton: boolean;

    constructor(private session: SessionService) {
    }

    showMenu(): boolean {
        return !this.readOnly && !this.hideButton;
    }

    onMenuItemClick(menuItem: IActionItem, payload?: any) {
        if (menuItem.enabled) {
            this.session.onAction(menuItem, payload);
        }
    }

}
