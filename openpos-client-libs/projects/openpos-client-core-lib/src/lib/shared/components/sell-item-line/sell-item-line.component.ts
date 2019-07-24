import { IActionItem } from '../../../core/actions/action-item.interface';
import { Component, Input } from '@angular/core';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { ActionService } from '../../../core/actions/action.service';

@Component({
    selector: 'app-sell-item-line',
    templateUrl: './sell-item-line.component.html',
    styleUrls: ['./sell-item-line.component.scss']
})
export class SellItemLineComponent {

    @Input() item: ISellItem;
    @Input() readOnly: boolean;
    @Input() hideButton: boolean;

    constructor(private actionService: ActionService) {
    }

    showMenu(): boolean {
        return !this.readOnly && !this.hideButton;
    }

    onMenuItemClick(menuItem: IActionItem, payload?: any) {
        if (menuItem.enabled) {
            this.actionService.doAction(menuItem, payload);
        }
    }

}
