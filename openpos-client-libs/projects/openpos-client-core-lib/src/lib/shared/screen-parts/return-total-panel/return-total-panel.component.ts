import { ScreenPartComponent } from '../screen-part';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Component } from '@angular/core';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ReturnTotalPanelInterface } from './return-total-panel.interface';

@ScreenPart({
    name: 'ReturnTotalPanel'
})
@Component({
    selector: 'app-return-total-panel',
    templateUrl: './return-total-panel.component.html',
    styleUrls: ['./return-total-panel.component.scss']
})
export class ReturnTotalPanelComponent extends ScreenPartComponent<ReturnTotalPanelInterface> {

    private loyaltyIconToken = '${icon}';
    public loyaltyBefore: string;
    public loyaltyAfter: string;

    screenDataUpdated() {
        if (this.screenData.loyaltyButton) {
            const title = this.screenData.loyaltyButton.title as string;
            const parts = title.split(this.loyaltyIconToken);
            if (parts && parts.length > 0) {
                this.loyaltyBefore = parts[0].trim();
                if (parts.length > 1) {
                    this.loyaltyAfter = parts[1].trim();
                }
            }
        }
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.doAction(menuItem);
    }

}
