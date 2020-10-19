import { Component, Input } from '@angular/core';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';


@Component({
    selector: 'app-mobile-item',
    templateUrl: './mobile-item.component.html',
    styleUrls: ['./mobile-item.component.scss']
})
export class MobileItemComponent {
    item: ISellItem;
    filteredAdditionalLabels:{label: string, value: string} [];
    @Input() set _item(item: ISellItem){
        this.item = item;
        this.filterAdditionalLabels();
    }
    @Input() expanded = true;

    constructor(public actionService: ActionService, public session: SessionService) { }

    public doItemAction(action: IActionItem, payload: number) {
        this.actionService.doAction(action, [payload]);
    }

    public isMenuItemEnabled(m: IActionItem): boolean {
        let enabled = m.enabled;
        if (m.action.startsWith('<') && this.session.isRunningInBrowser()) {
            enabled = false;
        }
        return enabled;
    }

    public filterAdditionalLabels() {
        this.filteredAdditionalLabels = this.item.additionalLabels.filter(additionalLabel =>
            this.item.collapsedAdditionalLabels.find(
                label => label == additionalLabel.label
            )
        )
    }
}
