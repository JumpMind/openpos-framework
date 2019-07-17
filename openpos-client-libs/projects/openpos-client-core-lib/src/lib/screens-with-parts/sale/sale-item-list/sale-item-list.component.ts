import { SaleItemListInterface } from './sale-item-list.interface';
import { Component, Injector } from '@angular/core';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { SelectableItemListComponentConfiguration } from '../../../shared/components/selectable-item-list/selectable-item-list.component';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { SelectionMode } from '../../../core/interfaces/selection-mode.enum';
import { Observable, BehaviorSubject } from 'rxjs';
import { ISelectableListData } from '../../../shared/components/selectable-item-list/selectable-list-data.interface';

@Component({
    selector: 'app-sale-item-list',
    templateUrl: './sale-item-list.component.html',
    styleUrls: ['./sale-item-list.component.scss']})
export class SaleItemListComponent extends ScreenPartComponent<SaleItemListInterface> {

    items: ISellItem[];
    selectedItems: ISellItem[] = new Array<ISellItem>();
    listConfig = new SelectableItemListComponentConfiguration();
    listData = new Observable<ISelectableListData<ISellItem>>();

    private screenData$ = new BehaviorSubject<ISelectableListData<ISellItem>>(null);
    constructor( injector: Injector) {
        super(injector);
        this.listData = this.screenData$;
    }

    screenDataUpdated() {
        const allItems = new Map<number, ISellItem>();
        const allDisabledItems = new Map<number, ISellItem>();
        for (let i = 0; i < this.screenData.items.length; i++) {
            const item = this.screenData.items[i];
            allItems.set(i, item);
            if (!item.enabled) {
                allDisabledItems.set(i, item);
            }
        }

        this.screenData$.next({
            items: allItems,
            disabledItems: allDisabledItems,
        } as ISelectableListData<ISellItem>
        );

        this.selectedItems = this.screenData.items
            .filter(item => this.screenData.selectedItemIndexes.find(index => item.index === index) !== undefined);

        this.listConfig = new SelectableItemListComponentConfiguration();
        this.listConfig.selectionMode = SelectionMode.Multiple;
        this.listConfig.numItemsPerPage = Number.MAX_VALUE;
        this.listConfig.totalNumberOfItems = this.screenData.items.length;

        this.items = this.screenData.items;
    }

    public onItemListChange(event: any[]): void {
        this.screenData.selectedItemIndexes = event;
        this.actionService.doAction({action: 'SelectedItemsChanged'}, this.screenData.selectedItemIndexes);
    }

    public onMenuAction(event: any) {
        if (event.menuItem && event.payload) {
            this.doAction(event.menuItem, event.payload);
        } else {
            this.doAction(event);
        }
    }
}
