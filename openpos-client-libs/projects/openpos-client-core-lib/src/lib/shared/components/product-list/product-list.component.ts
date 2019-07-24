import { Component, Input, Output, EventEmitter } from '@angular/core';
import { IItem } from '../../../core/interfaces/item.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { SelectionMode } from '../../../core/interfaces/selection-mode.enum';

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent {
    @Input() items: IItem[];
    @Input() menuItemActions: IActionItem[] = [];
    @Input() showItemIcon = true;
    @Input() showItemMenu = false;
    @Input() itemClass: string;
    @Output() itemClick = new EventEmitter<ItemClickAction>();
    @Output() itemSelected = new EventEmitter<ItemClickAction>();
    @Output() menuItemClick = new EventEmitter<MenuClickAction>();
    @Input() selectionMode: SelectionMode = SelectionMode.Single;

    selectedItems: number[] = [];

    onItemClick(item: IItem, event: any): void {
        this.itemClick.emit({item, event});
    }

    onSelectedOptionsChange(event: any) {
        this.selectedItems = event;
    }

    onItemSelected(item: IItem, event: any): void {
        item.selected = this.selectedItems.find(n => n === item.index) >= 0;
        if (this.selectionMode === SelectionMode.SingleCheckbox) {
            this.selectedItems = item.selected ? [item.index] : [];
        }

        this.itemSelected.emit({item, event});
    }

    onMenuItemClick(item: IItem, menuItem: IActionItem, event: any): void {
        this.menuItemClick.emit({item, menuItem, event});
    }

    isMultipleSelectionMode(): boolean {
        return this.selectionMode === SelectionMode.Multiple || this.selectionMode === SelectionMode.SingleCheckbox;
    }

}

export interface ItemClickAction {
    item: IItem;
    event: any;
}

export interface MenuClickAction {
    item: IItem;
    menuItem: IActionItem;
    event: any;
}
