import { SelectionMode } from './../selectionmode';
import { IMenuItem } from './../imenuitem';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { IScreen } from '../iscreen';
import { IItem } from '../iitem';

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent {
    @Input() items: IItem[];
    @Input() menuItemActions: IMenuItem[] = [];
    @Input() showItemIcon = true;
    @Input() showItemMenu = false;
    @Input() itemClass: string;
    @Output() itemClick = new EventEmitter<ItemClickAction>();
    @Output() itemSelected = new EventEmitter<ItemClickAction>();
    @Output() menuItemClick = new EventEmitter<MenuClickAction>();
    @Input() selectionMode: SelectionMode = SelectionMode.Single;
    selectedItems: number[] = [];

    onItemClick(item: IItem, event: any): void {
        console.log(`productList.onItemClick: ${item}`);
        this.itemClick.emit({item, event});
    }

    onSelectedOptionsChange(event: any) {
        this.selectedItems = event;
    }

    onItemSelected(item: IItem, event: any): void {
        item.selected = this.selectedItems.find(n => n === item.index) >= 0;
        this.itemSelected.emit({item, event});
    }

    onMenuItemClick(item: IItem, menuItem: IMenuItem, event: any): void {
        console.log(`productList.onMenuItemClick: ${menuItem}`);
        this.menuItemClick.emit({item, menuItem, event});
    }

    isMultipleSelectionMode(): boolean {
        return this.selectionMode === SelectionMode.Multiple;
    }
}

export interface ItemClickAction {
    item: IItem;
    event: any;
}

export interface MenuClickAction {
    item: IItem;
    menuItem: IMenuItem;
    event: any;
}
