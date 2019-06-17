import { Component, ViewChildren, QueryList, ElementRef, AfterViewInit } from '@angular/core';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { SelectionListInterface } from './selection-list.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Configuration } from '../../configuration/configuration';
import { Observable, of, merge, Subject, BehaviorSubject } from 'rxjs';
import { ISelectableListData } from '../../shared/components/selectable-item-list/selectable-list-data.interface';
import { MessageProvider } from '../../shared/providers/message.provider';
import { filter, map } from 'rxjs/operators';
import { ISelectionListItem } from './selection-list-item.interface';

@ScreenComponent({
    name: 'SelectionList'
})
@Component({
    selector: 'app-selection-list-screen',
    templateUrl: './selection-list-screen.component.html',
    styleUrls: ['./selection-list-screen.component.scss']
})
export class SelectionListScreenComponent extends PosScreen<SelectionListInterface> implements AfterViewInit {
    @ViewChildren('items') private items: QueryList<ElementRef>;

    listData: Observable<ISelectableListData<ISelectionListItem>>;
    listConfig: SelectableItemListComponentConfiguration;
    selectionMode: SelectionMode;
    index = -1;
    indexes = [];
    lastSelection = -1;

    private screenData$ = new BehaviorSubject<ISelectableListData<ISelectionListItem>>(null);

    constructor() {
        super();
        this.listData = merge( this.session.getMessages('UIData').pipe(
            filter( d => d.dataType === 'SelectionListData'),
            map((d) => {
                const items = new Map<string, ISelectionListItem>();
                const disabledItems = new Map<string, ISelectionListItem>();
                Object.getOwnPropertyNames(d.items).forEach(element => {
                    items.set( element, d.items[element]);
                });
                Object.getOwnPropertyNames(d.disabledItems).forEach(element => {
                    disabledItems.set( element, d.disabledItems[element]);
                });
                d.items = items;
                d.disabledItems = disabledItems;
                return d;
            })
        ), this.screenData$);
    }

    buildScreen() {
        if (this.screen.selectionList && this.screen.selectionList.length > 0) {
            const allItems = new Map<number, ISelectionListItem>();
            const allDisabledItems = new Map<number, ISelectionListItem>();
            for (let i = 0; i < this.screen.selectionList.length; i++) {
                const item = this.screen.selectionList[i];
                allItems.set(i, item);
                if (!item.enabled) {
                    allDisabledItems.set(i, item);
                }
            }
            this.screenData$.next({
                items: allItems,
                disabledItems: allDisabledItems,
            } as ISelectableListData<ISelectionListItem>
            );
        }

        this.listConfig = new SelectableItemListComponentConfiguration();
        if (this.screen.numberItemsPerPage <= 0) {
            this.listConfig.numItemsPerPage = Number.MAX_VALUE;
        } else {
            this.listConfig.numItemsPerPage = this.screen.numberItemsPerPage;
        }
        if (this.screen.selectionList && this.screen.selectionList.length > 0) {
            this.listConfig.totalNumberOfItems = this.screen.selectionList.length;
        } else {
            this.listConfig.totalNumberOfItems = this.screen.numberTotalItems;
        }
        this.listConfig.defaultSelectItemIndex = this.screen.defaultSelectItemIndex;
        this.listConfig.selectionMode = this.screen.multiSelect ? SelectionMode.Multiple : SelectionMode.Single;
        this.listConfig.fetchDataAction = this.screen.fetchDataAction;
    }

    ngAfterViewInit() {
        this.items.changes.subscribe(() => {
            console.log('changed');
        });
    }

    public onItemListChange(event: any[]): void {
        this.indexes = event;
    }

    public onItemChange(event: any): void {
        this.index = event;

        if (this.screen.selectionChangedAction && this.index !== this.lastSelection) {
            this.lastSelection = this.index;
            this.session.onAction(this.screen.selectionChangedAction, this.index);
        }
    }

    public doMenuItemAction(menuItem: IActionItem) {
        if (this.screen.multiSelect) {
            this.session.onAction(menuItem, this.indexes);
        } else {
            this.session.onAction(menuItem, this.index);
        }
    }

    public keybindsEnabled(menuItem: IActionItem): boolean {
        return Configuration.enableKeybinds && menuItem.keybind && menuItem.keybind !== 'Enter';
    }
}
