import { SaleItemListInterface } from './sale-item-list.interface';
import { Component } from '@angular/core';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { SelectableItemListComponentConfiguration } from '../../../shared/components/selectable-item-list/selectable-item-list.component';
import { MessageProvider } from '../../../shared/providers/message.provider';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { SelectionMode } from '../../../core/interfaces/selection-mode.enum';

@Component({
    selector: 'app-sale-item-list',
    templateUrl: './sale-item-list.component.html',
    styleUrls: ['./sale-item-list.component.scss']})
export class SaleItemListComponent extends ScreenPartComponent<SaleItemListInterface> {

    items: ISellItem[];
    selectedItems: ISellItem[] = new Array<ISellItem>();
    listConfig = new SelectableItemListComponentConfiguration<ISellItem>();

    constructor( messageProivder: MessageProvider) {
        super(messageProivder);
    }

    screenDataUpdated() {
        this.selectedItems = this.screenData.items.filter(item => this.screenData.selectedItemIndexes.find(index => item.index === index) !== undefined);
        this.listConfig = new SelectableItemListComponentConfiguration<ISellItem>();
        this.listConfig.selectionMode = SelectionMode.Multiple;
        this.listConfig.numResultsPerPage = Number.MAX_VALUE;
        this.listConfig.items = this.screenData.items;
        this.items = this.screenData.items;
    }

    public onItemListChange(items: ISellItem[]): void {
        this.screenData.selectedItemIndexes = items.map(item => item.index);
        this.sessionService.onValueChange('SelectedItemsChanged', this.screenData.selectedItemIndexes);
    }

    public onMenuAction(event: any) {
        if (event.menuItem && event.payload) {
            this.onMenuItemClick(event.menuItem, event.payload);
        } else {
            this.onMenuItemClick(event);
        }
    }
}
