import {Component} from '@angular/core';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import {PosScreen} from "../pos-screen/pos-screen.component";
import {IActionItem} from "../../core/actions/action-item.interface";
import {Observable} from "rxjs/internal/Observable";
import {SelectableItemListComponentConfiguration} from "../../shared/components/selectable-item-list/selectable-item-list.component";
import {SelectionMode} from "../../core/interfaces/selection-mode.enum";
import {CustomerSearchResultDialogInterface, ICustomerDetails} from "./customer-search-result-dialog.interface";
import {ISelectableListData} from "../../shared/components/selectable-item-list/selectable-list-data.interface";

@DialogComponent({
    name: 'CustomerSearchResultDialog'
})
@Component({
    selector: 'app-customer-search-result-dialog',
    templateUrl: './customer-search-result-dialog.component.html',
    styleUrls: ['./customer-search-result-dialog.component.scss']
})
export class CustomerSearchResultDialogComponent extends PosScreen<CustomerSearchResultDialogInterface> {

    index = -1;
    listData: Observable<ISelectableListData<ICustomerDetails>>;
    listConfig: SelectableItemListComponentConfiguration;

    buildScreen() {
        this.populateListData();
        this.defineConfiguration();
    }

     populateListData() {
        let allItems = this.transformResultsToMap();
        let disabledItems = new Map<number, ICustomerDetails>();

        this.listData = new Observable<ISelectableListData<ICustomerDetails>>((observer) => {
            observer.next({
                items: allItems,
                disabledItems: disabledItems
            } as ISelectableListData<ICustomerDetails>);
        });
    }

    transformResultsToMap() {
        let allItems = new Map<number, ICustomerDetails>();
        this.screen.results.forEach((value, index) => {
           allItems.set(index, value);
        });

        return allItems;
    }

    defineConfiguration(): void{
        this.listConfig = new SelectableItemListComponentConfiguration();
        this.listConfig.selectionMode = SelectionMode.Single;
        this.listConfig.numItemsPerPage = Number.MAX_VALUE;
        this.listConfig.totalNumberOfItems = this.screen.results.length;
    }

    public onItemChange(event: any): void {
        this.index = event;
    }

    public doSelectionButtonAction(menuItem: IActionItem) {
        this.doAction(menuItem, this.index);
    }
}