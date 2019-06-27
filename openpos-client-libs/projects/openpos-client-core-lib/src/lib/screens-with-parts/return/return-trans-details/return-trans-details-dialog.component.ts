import { Configuration } from './../../../configuration/configuration';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { SelectionMode } from './../../../core/interfaces/selection-mode.enum';
import { ISellItem } from './../../../core/interfaces/sell-item.interface';
import { SelectionListScreenComponent } from '../../selection-list/selection-list-screen.component';
import { Component, Input } from '@angular/core';
import { DialogComponent } from '../../../shared/decorators/dialog-component.decorator';
import { SelectableItemListComponentConfiguration } from '../../../shared/components/selectable-item-list/selectable-item-list.component';
import { ReturnTransDetailsInterface } from './return-trans-detals.interface';
import { PosScreen } from '../../../screens-deprecated/pos-screen/pos-screen.component';

@DialogComponent({
    name: 'ReturnTransDetailDialog'
})
@Component({
    selector: 'app-return-trans-details-dialog',
    templateUrl: './return-trans-details-dialog.component.html',
    styleUrls: ['./return-trans-details-dialog.component.scss']
})
export class ReturnTransDetailsDialogComponent extends PosScreen<ReturnTransDetailsInterface>  {

    listConfig: SelectableItemListComponentConfiguration<ISellItem>;
    selectionButton: IActionItem;
    index = -1;

    public onItemChange(event: any): void {
        this.index = this.screen.items.indexOf(event);
    }

    buildScreen() {
        this.selectionButton = this.screen.selectionButton;
        this.listConfig = new SelectableItemListComponentConfiguration<ISellItem>();
        this.listConfig.selectionMode = SelectionMode.Single;
        this.listConfig.numResultsPerPage = Number.MAX_VALUE;
        this.listConfig.items = this.screen.items;
        this.listConfig.disabledItems = this.screen.items.filter(e => !e.enabled);
    }

    public doMenuItemAction(menuItem: IActionItem) {
        if (this.index > -1) {
            this.session.onAction(menuItem, this.index);
        }
    }

    public keybindsEnabled(menuItem: IActionItem): boolean {
        return Configuration.enableKeybinds && menuItem.keybind && menuItem.keybind !== 'Enter';
    }
}
