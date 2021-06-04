import {
    OnDestroy, Component, Input, Output, EventEmitter, ContentChild, TemplateRef, ElementRef, OnInit,
    ViewChildren, QueryList, AfterViewInit, AfterContentInit, AfterViewChecked, SimpleChanges
} from '@angular/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { Observable } from 'rxjs';
import { ISelectableListData } from './selectable-list-data.interface';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';
import { SelectionMode } from '../../../core/interfaces/selection-mode.enum';
import { SessionService } from '../../../core/services/session.service';
import { ActionService } from '../../../core/actions/action.service';

export class SelectableItemListComponentConfiguration {
    numItemsPerPage: number;
    totalNumberOfItems: number;
    defaultSelectItemIndex: number;
    selectionMode: SelectionMode;
    fetchDataAction: string;
}

@Component({
    selector: 'app-selectable-item-list',
    templateUrl: './selectable-item-list.component.html',
    styleUrls: ['./selectable-item-list.component.scss']
})
export class SelectableItemListComponent<ItemType> implements OnDestroy, OnInit, AfterViewInit {
    @ContentChild(TemplateRef) itemTemplate: TemplateRef<ElementRef>;
    @ViewChildren('items') private itemsRef: QueryList<ElementRef>;

    @Input() keyboardControl = true;
    @Input() defaultSelect = false;
    @Input() listData: Observable<ISelectableListData<ItemType>>;

    @Input()
    set configuration(config: SelectableItemListComponentConfiguration) {
        this._config = config;
        this.updateResultsToShow();
    }
    get configuration(): SelectableItemListComponentConfiguration {
        return this._config;
    }

    @Input()
    set selectedItem( item: ItemType ) {
        this._selectedItem = item;
        this.updateKeySubscriptions();
    }
    get selectedItem(): ItemType {
        return this._selectedItem;
    }
    @Output() selectedItemChange = new EventEmitter<number>();

    @Input()
    set selectedItemList( itemList: ItemType[] ) {
        this._selectedItemList = itemList;
        this.updateKeySubscriptions();
    }
    get selectedItemList(): ItemType[] {
        return this._selectedItemList;
    }
    @Output() selectedItemListChange = new EventEmitter<number[]>();

    numberOfPages: number;
    currentPage = 1;
    useDefaultSelectIndexToStart = true;
    items: Map<number, ItemType>;
    disabledItems: Map<number, ItemType>;
    itemsToShow: ItemType[];
    itemPageMap: Map<number, ItemType[]> = new Map<number, ItemType[]>();
    disabledItemPageMap: Map<number, Map<number, ItemType>> = new Map<number, Map<number, ItemType>>();
    scrollToIndex: number;

    private _selectedItem: ItemType;
    private _selectedItemList = new Array<ItemType>();
    private _config: SelectableItemListComponentConfiguration;

    private selectedItemSubscription: Subscription;
    private subscriptions = new Subscription();

    constructor(private keyPresses: KeyPressProvider, private actionService: ActionService, private session: SessionService) {

        // we only want to be subscribed for keypresses when we have selected items
        // so watch the selected item changes and add remove the key bindings.
        this.subscriptions.add(this.selectedItemChange.subscribe(item => {
            this.updateKeySubscriptions();
        }));

        this.subscriptions.add(this.selectedItemListChange.subscribe(list => {
            this.updateKeySubscriptions();
        }));

        this.subscriptions.add(this.keyPresses.subscribe( 'ArrowDown', 1, event => {
            // ignore repeats and check configuration
            if ( event.repeat || !Configuration.enableKeybinds || !this.keyboardControl ) {
                return;
            }
            if ( event.type === 'keydown') {
                this.handleArrowKey(event);
            }
        }));

        this.subscriptions.add(
            this.keyPresses.subscribe( 'ArrowUp', 1, event => {
                // ignore repeats and check configuration
                if ( event.repeat || !Configuration.enableKeybinds || !this.keyboardControl ) {
                    return;
                }
                if ( event.type === 'keydown') {
                    this.handleArrowKey(event);
                }
            })
        );

        this.subscriptions.add(
            this.keyPresses.subscribe('ArrowRight', 2, event => {
                if (event.repeat || !Configuration.enableKeybinds || !this.keyboardControl) {
                    return;
                }
                if (event.type === 'keydown') {
                    if (this.currentPage < this.numberOfPages) {
                        this.onNextPage();
                    }
                }
            })
        );

        this.subscriptions.add(
            this.keyPresses.subscribe('ArrowLeft', 2, event => {
                if (event.repeat || !Configuration.enableKeybinds || !this.keyboardControl) {
                    return;
                }
                if (event.type === 'keydown') {
                    if (this.currentPage > 1) {
                        this.onPrevPage();
                    }
                }
            })
        );
    }

    private updateKeySubscriptions() {
        if (((this.selectedItemList && this.selectedItemList.length) || this.selectedItem) && !this.selectedItemSubscription) {
            this.buildKeySubscriptions();
        } else if ( !((this.selectedItemList  && this.selectedItemList.length) || this.selectedItem) && this.selectedItemSubscription ) {
            this.selectedItemSubscription.unsubscribe();
            this.selectedItemSubscription = null;
        }
    }

    private buildKeySubscriptions() {
        this.selectedItemSubscription =
            this.keyPresses.subscribe( 'Escape', 1, event => {
                // ignore repeats and check configuration
                if ( event.repeat || !Configuration.enableKeybinds || !this.keyboardControl ) {
                    return;
                }
                if ( event.type === 'keydown') {
                    this.handleEscape(event);
                }
            });
    }

    ngOnInit(): void {
        this.subscriptions.add(this.listData.subscribe((selectableListData: ISelectableListData<ItemType>) => {
            if (selectableListData != null) {
                this.items = selectableListData.items;
                this.disabledItems = selectableListData.disabledItems;
                this.updateResultsToShow();
            }
        }));

        this.numberOfPages = this.configuration.totalNumberOfItems / this.configuration.numItemsPerPage;
        if (this.defaultSelect && this.useDefaultSelectIndexToStart && this.configuration.defaultSelectItemIndex) {
            this.currentPage = Math.trunc(this.configuration.defaultSelectItemIndex / this.configuration.numItemsPerPage) + 1;
        }

        if (this.configuration.fetchDataAction) {
            this.actionService.doAction({ action: this.configuration.fetchDataAction, doNotBlockForResponse: true }, this.currentPage);
        }
        console.dir(this);
    }

    ngAfterViewInit(): void {
        this.itemsRef.changes.subscribe(() => {
            if (this.scrollToIndex) {
                this.scrollToItem(this.scrollToIndex);
            }
        });
    }

    ngOnDestroy(): void {
        if (this.subscriptions) {
            this.subscriptions.unsubscribe();
        }
        if (this.selectedItemSubscription) {
            this.selectedItemSubscription.unsubscribe();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.updateResultsToShow();
    }

    updateResultsToShow(): void {
        if (this.items) {
            if (this.items.size === this.configuration.totalNumberOfItems) {
                this.itemsToShow = Array.from(this.items.values()).slice((this.currentPage - 1) *
                this.configuration.numItemsPerPage, this.configuration.numItemsPerPage * this.currentPage);
            } else if (this.isPageSavedInMap()) {
                this.itemsToShow = this.itemPageMap.get(this.currentPage);
                if (this.disabledItemPageMap && this.disabledItemPageMap.get(this.currentPage) !== undefined
                    && this.disabledItemPageMap.get(this.currentPage) !== null) {
                    this.disabledItems = this.disabledItemPageMap.get(this.currentPage);
                }
            } else {
                this.itemsToShow = Array.from(this.items.values());
                if (this.itemsToShow.length > 0) {
                    this.itemPageMap.set(this.currentPage, this.itemsToShow);
                }
                if (this.disabledItems.size > 0) {
                    this.disabledItemPageMap.set(this.currentPage, this.disabledItems);
                }
            }

            if (this.defaultSelect && this.useDefaultSelectIndexToStart
                && this.configuration.defaultSelectItemIndex !== undefined && this.configuration.defaultSelectItemIndex !== null) {
                this.scrollToIndex = this.configuration.defaultSelectItemIndex % this.configuration.numItemsPerPage;
                let originalItemIndex = this.scrollToIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                while (this.disabledItems.get(originalItemIndex)) {
                    this.scrollToIndex++;
                    originalItemIndex = this.scrollToIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                }
                if (this.itemsToShow.length > this.scrollToIndex && this.scrollToIndex > -1) {
                    switch (this.configuration.selectionMode) {
                        case SelectionMode.Single:
                            this.selectedItem = this.itemsToShow[this.scrollToIndex];
                            this.scrollToItem(this.scrollToIndex);
                            this.selectedItemChange.emit(originalItemIndex);
                            break;
                        case SelectionMode.Multiple:
                            this.selectedItemList.push(this.itemsToShow[this.scrollToIndex]);
                            this.scrollToItem(this.scrollToIndex);
                            const indexes = [];
                            this.selectedItemList.forEach(i =>
                                indexes.push(this.itemsToShow.indexOf(i) + ((this.currentPage - 1) * this.configuration.numItemsPerPage)));
                            this.selectedItemListChange.emit(indexes);
                            break;
                    }
                }
            } else if (this.defaultSelect && this.useDefaultSelectIndexToStart &&  this.itemsToShow.length === 1) {
                this.scrollToIndex = 0;
                const originalItemIndex = this.scrollToIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                if (!this.disabledItems.get(originalItemIndex)) {
                    switch (this.configuration.selectionMode) {
                        case SelectionMode.Single:
                            this.selectedItem = this.itemsToShow[0];
                            this.selectedItemChange.emit(originalItemIndex);
                            break;
                        case SelectionMode.Multiple:
                            this.selectedItemList.push(this.itemsToShow[0]);
                            const indexes = [originalItemIndex];
                            this.selectedItemListChange.emit(indexes);
                            break;
                    }
                }
            }
        }

    }

    onNextPage() {
        this.currentPage++;
        this.useDefaultSelectIndexToStart = false;
        if (this.isPageSavedInMap() || !this.configuration.fetchDataAction) {
            this.updateResultsToShow();
        } else {
            this.actionService.doAction({action: this.configuration.fetchDataAction, doNotBlockForResponse: true}, this.currentPage);
        }
    }

    onPrevPage() {
        this.currentPage--;
        this.useDefaultSelectIndexToStart = false;
        if (this.isPageSavedInMap() || !this.configuration.fetchDataAction) {
            this.updateResultsToShow();
        } else {
            this.actionService.doAction({action: this.configuration.fetchDataAction, doNotBlockForResponse: true}, this.currentPage);
        }
    }

    onItemClick(item: ItemType, event: any) {
        // look for block-selection attribute and don't do the selection if we find it in our path
        const originalItemIndex = this.itemsToShow.indexOf(item) + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
        if ((event.path && event.path.find(element => element.attributes && element.attributes.getNamedItem('block-selection'))) ||
            this.disabledItems.get(originalItemIndex)) {
            return;
        }

        switch (this.configuration.selectionMode) {
            case SelectionMode.Multiple:
                const index = this.selectedItemList.indexOf(item);
                if (index >= 0) {
                    this.selectedItemList.splice(index, 1);
                    this.scrollToItem(index);
                } else {
                    this.selectedItemList.push(item);
                    this.scrollToItem(this.itemsToShow.indexOf(item));
                }
                const indexes = [];
                this.selectedItemList.forEach(i =>
                    indexes.push(this.itemsToShow.indexOf(i) + ((this.currentPage - 1) * this.configuration.numItemsPerPage)));
                this.selectedItemListChange.emit(indexes);
                break;
            case SelectionMode.Single:
                const itemIndex = this.itemsToShow.indexOf(item);
                this.selectedItem = item;
                this.scrollToItem(itemIndex);
                this.selectedItemChange.emit(itemIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage));
                break;
        }
    }

    scrollToItem(index: number) {
        let indexToView = index;
        if (this.configuration.numItemsPerPage !== 0) {
            indexToView -= Math.trunc(index / this.configuration.numItemsPerPage) * this.configuration.numItemsPerPage;
        }
        if (this.itemsRef && this.itemsRef.toArray()[indexToView]) {
            this.itemsRef.toArray()[indexToView].nativeElement.scrollIntoView({block: 'center'});
        }
    }

    isItemSelected(item: ItemType): boolean {
        switch (this.configuration.selectionMode) {
            case SelectionMode.Multiple:
                return this.selectedItemList.includes(item);
            case SelectionMode.Single:
                return this.selectedItem === item;
        }
    }

    isPageSavedInMap(): boolean {
        return this.itemPageMap && this.itemPageMap.get(this.currentPage) !== undefined
            && this.itemPageMap.get(this.currentPage) !== null;
    }

    handleEscape(event: KeyboardEvent) {
        switch (this.configuration.selectionMode) {
            case SelectionMode.Single:
                if (this.selectedItem != null) {
                    this.selectedItem = null;
                    this.selectedItemChange.emit(-1);
                    event.preventDefault();
                }
                break;
            case SelectionMode.Multiple:
                if (this.selectedItemList.length > 0) {
                    this.selectedItemList.length = 0;
                    this.selectedItemListChange.emit([]);
                    event.preventDefault();
                }
                break;
        }
    }

    handleArrowKey(event: KeyboardEvent) {
        let bound = false;
        let direction = 1;
        if (event.key === 'ArrowDown') {
          direction = 1;
        } else if (event.key === 'ArrowUp') {
          direction = -1;
        } else {
          return;
        }
        // debugger;
        const itemIndexToSelect = -1;

        switch (this.configuration.selectionMode) {
            case SelectionMode.Single:
                let currentListIndex = this.itemsToShow.findIndex(item => item === this.selectedItem);
                if (currentListIndex === -1  && direction === -1) {
                    return; // only allow key down to start selecting on the list.
                }
                let newIndex = currentListIndex + direction;
                let originalItemIndex = newIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                while (this.disabledItems.get(originalItemIndex)) {
                    newIndex = newIndex + direction;
                    originalItemIndex = newIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                }

                if (newIndex < 0) {
                    newIndex = 0;
                    originalItemIndex = newIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                    while (this.disabledItems.get(originalItemIndex)) {
                        newIndex++;
                        originalItemIndex = newIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                    }
                }
                if (this.itemsToShow.length > newIndex) {
                    this.selectedItem = this.itemsToShow[newIndex];
                    this.scrollToItem(newIndex);
                    this.selectedItemChange.emit(originalItemIndex);
                }
                break;
            case SelectionMode.Multiple:
                if (this.selectedItemList && this.selectedItemList.length === 1) {
                    currentListIndex = this.itemsToShow.findIndex(item => item === this.selectedItemList[0]);
                    newIndex = currentListIndex + direction;
                    originalItemIndex = newIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                    while (this.disabledItems.get(originalItemIndex)) {
                        newIndex = newIndex + direction;
                        originalItemIndex = newIndex + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                    }
                    if (this.itemsToShow.length > newIndex && newIndex > -1) {
                        this.selectedItemList = [this.itemsToShow[newIndex]];
                        this.scrollToItem(newIndex);
                        this.selectedItemListChange.emit([newIndex]);
                    }
                } else if (this.itemsToShow.length > 0) { // only allow key down to start selecting on the list.
                    let index = 0;
                    if (direction === -1) {
                        index = this.itemsToShow.length - 1;
                    }
                    originalItemIndex = index + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                    while (this.disabledItems.get(originalItemIndex)) {
                        index = index + direction;
                        originalItemIndex = index + ((this.currentPage - 1) * this.configuration.numItemsPerPage);
                    }
                    if (this.itemsToShow.length > index && index > -1) {
                        this.selectedItemList = [this.itemsToShow[index]];
                        this.scrollToItem(index);
                        this.selectedItemListChange.emit([index]);
                    }

                }
                break;
        }

        if (itemIndexToSelect > -1) {
          bound = true;
          this.selectedItem = this.itemsToShow[itemIndexToSelect];
          this.selectedItemList = [this.itemsToShow[itemIndexToSelect]];
        }

        if (bound) {
          event.preventDefault();
        }
    }

}
