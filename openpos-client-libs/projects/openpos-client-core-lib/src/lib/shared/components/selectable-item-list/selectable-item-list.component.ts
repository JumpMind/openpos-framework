import { Component, Input, ContentChild, TemplateRef, ElementRef, Output, EventEmitter, OnDestroy } from '@angular/core';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Subscription } from 'rxjs';
import { Configuration } from '../../../configuration/configuration';
import { SelectionMode } from '../../../core/interfaces/selection-mode.enum';

export class SelectableItemListComponentConfiguration<ItemType> {
    numResultsPerPage: number;
    items: ItemType[];
    disabledItems: ItemType[] = [];
    defaultSelectItemIndex: number;
    selectionMode: SelectionMode;
}

@Component({
    selector: 'app-selectable-item-list',
    templateUrl: './selectable-item-list.component.html',
    styleUrls: ['./selectable-item-list.component.scss']
})
export class SelectableItemListComponent<ItemType> implements OnDestroy {

    @ContentChild(TemplateRef) itemTemplate: TemplateRef<ElementRef>;

    @Input() defaultSelect = false;
    @Input() keyboardControl = true;
    @Input()
    set configuration(config: SelectableItemListComponentConfiguration<ItemType>) {
        this._config = config;
        this.updateResultsToShow();
    }
    get configuration(): SelectableItemListComponentConfiguration<ItemType> {
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
    @Output() selectedItemChange = new EventEmitter<ItemType>();

    @Input()
    set selectedItemList ( itemList: ItemType[] ) {
        this._selectedItemList = itemList;
        this.updateKeySubscriptions();
    }
    get selectedItemList (): ItemType[] {
        return this._selectedItemList;
    }
    @Output() selectedItemListChange = new EventEmitter<ItemType[]>();

    numberOfPages: number;
    itemsToShow: ItemType[];
    disabledItems: ItemType[];
    currentPage = 1;

    private _selectedItem: ItemType;
    private _selectedItemList = new Array<ItemType>();
    private _config: SelectableItemListComponentConfiguration<ItemType>;

    private subscription: Subscription;
    private selectedItemSubscription: Subscription;

    constructor(private keyPresses: KeyPressProvider) {
        // we only want to be subscribed for keypresses when we have selected items
        // so watch the selected item changes and add remove the key bindings.
        this.selectedItemChange.subscribe( item => {
            this.updateKeySubscriptions();
        });

        this.selectedItemListChange.subscribe( list => {
            this.updateKeySubscriptions();
        });

        this.subscription = this.keyPresses.subscribe( 'ArrowDown', 1, event => {
            // ignore repeats and check configuration
            if ( event.repeat || !Configuration.enableKeybinds || !this.keyboardControl ) {
                return;
            }
            if ( event.type === 'keydown') {
                this.handleArrowKey(event);
            }
        });

        this.subscription.add(
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

        this.subscription.add(
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

        this.subscription.add(
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
        if ( (this.selectedItemList.length || this.selectedItem) && !this.selectedItemSubscription) {
            this.buildKeySubscriptions();
        } else if ( !(this.selectedItemList.length || this.selectedItem) && this.selectedItemSubscription ) {
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

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        if (this.selectedItemSubscription) {
            this.selectedItemSubscription.unsubscribe();
        }
    }

    updateResultsToShow(): void {
        if (this._config.items.length > 0) {
            this.numberOfPages = this._config.items.length / this._config.numResultsPerPage;
        }
        this.itemsToShow = this._config.items.slice((this.currentPage - 1) *
            this._config.numResultsPerPage, this._config.numResultsPerPage * this.currentPage);

        this.disabledItems = this._config.disabledItems;

        if (this.defaultSelect && this._config.defaultSelectItemIndex !== null && this._config.defaultSelectItemIndex !== undefined) {
            switch (this._config.selectionMode) {
                case SelectionMode.Single:
                    this.selectedItem = this.itemsToShow[this._config.defaultSelectItemIndex];
                    this.selectedItemChange.emit(this.selectedItem);
                    break;
                case SelectionMode.Multiple:
                    this.selectedItemList.push(this.itemsToShow[this._config.defaultSelectItemIndex]);
                    this.selectedItemListChange.emit(this.selectedItemList);
                    break;
            }
        } else if (this.defaultSelect && this.itemsToShow.length === 1) {
            switch (this._config.selectionMode) {
                case SelectionMode.Single:
                    this.selectedItem = this.itemsToShow[0];
                    this.selectedItemChange.emit(this.selectedItem);
                    break;
                case SelectionMode.Multiple:
                    this.selectedItemList.push(this.itemsToShow[0]);
                    this.selectedItemListChange.emit(this.selectedItemList);
                    break;
            }
        }
    }

    onNextPage() {
        this.currentPage++;
        this.updateResultsToShow();
    }

    onPrevPage() {
        this.currentPage--;
        this.updateResultsToShow();
    }

    onItemClick(item: ItemType, event: any) {
        // look for block-selection attribute and don't do the selection if we find it in our path
        if ( event.path.find(element => element.attributes && element.attributes.getNamedItem('block-selection')) ||
            this.disabledItems.includes(item)) {
            return;
        }
        switch (this._config.selectionMode) {
            case SelectionMode.Multiple:
                const i = this.selectedItemList.indexOf(item);
                if (i >= 0) {
                    this.selectedItemList.splice(i, 1);
                } else {
                    this.selectedItemList.push(item);
                }
                this.selectedItemListChange.emit(this.selectedItemList);
                break;
            case SelectionMode.Single:
                this.selectedItem = item;
                this.selectedItemChange.emit(item);
                break;
        }
    }

    isItemSelected(item: ItemType): boolean {
        switch (this._config.selectionMode) {
            case SelectionMode.Multiple:
                return this.selectedItemList.includes(item);
            case SelectionMode.Single:
                return this.selectedItem === item;
        }
    }

    handleEscape(event: KeyboardEvent) {
        switch (this._config.selectionMode) {
            case SelectionMode.Single:
                if (this.selectedItem != null) {
                    this.selectedItem = null;
                    this.selectedItemChange.emit(this.selectedItem);
                    event.preventDefault();
                }
                break;
            case SelectionMode.Multiple:
                if (this.selectedItemList.length > 0) {
                    this.selectedItemList.length = 0;
                    this.selectedItemListChange.emit(this.selectedItemList);
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

        switch (this._config.selectionMode) {
            case SelectionMode.Single:
                let currentListIndex = this.itemsToShow.findIndex(item => item === this.selectedItem);
                if (currentListIndex === -1  && direction === -1) {
                    return; // only allow key down to start selecting on the list.
                }
                let newIndex = currentListIndex + direction;
                while ( this.disabledItems.includes(this.itemsToShow[newIndex]) ) {
                    newIndex = newIndex + direction;
                }

                if (newIndex < 0) {
                    newIndex = 0;
                    while (this.disabledItems.includes(this.itemsToShow[newIndex])) {
                        newIndex++;
                    }
                }
                if (this.itemsToShow.length > newIndex) {
                    this.selectedItem = this.itemsToShow[newIndex];
                    this.selectedItemChange.emit(this.selectedItem);
                }
                break;
            case SelectionMode.Multiple:
                if (this.selectedItemList && this.selectedItemList.length === 1) {
                    currentListIndex = this.itemsToShow.findIndex(item => item === this.selectedItemList[0]);
                    newIndex = currentListIndex + direction;
                    while ( this.disabledItems.includes(this.itemsToShow[newIndex]) ) {
                        newIndex = newIndex + direction;
                    }
                    if (this.itemsToShow.length > newIndex && newIndex > -1) {
                        this.selectedItemList = [this.itemsToShow[newIndex]];
                        this.selectedItemListChange.emit(this.selectedItemList);
                    }
                } else if (this.itemsToShow.length > 0) { // only allow key down to start selecting on the list.
                    let index = 0;
                    if (direction === -1) {
                        index = this.itemsToShow.length - 1;
                    }
                    while (this.disabledItems.includes(this.itemsToShow[index])) {
                        index = index + direction;
                    }
                    this.selectedItemList = [this.itemsToShow[index]];
                    this.selectedItemListChange.emit(this.selectedItemList);
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
