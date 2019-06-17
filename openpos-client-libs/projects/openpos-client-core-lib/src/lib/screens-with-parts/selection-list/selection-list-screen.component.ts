import { Component, ViewChildren, QueryList, ElementRef, AfterViewInit } from '@angular/core';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { SelectionListInterface } from './selection-list.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Configuration } from '../../configuration/configuration';

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

    listConfig: SelectableItemListComponentConfiguration<any>;
    index = -1;
    lastSelection = -1;

    constructor() {
        super();
    }

    buildScreen() {
        this.listConfig = new SelectableItemListComponentConfiguration<any>();
        if (this.screen.multiSelect) {
            this.listConfig.selectionMode = SelectionMode.Multiple;
        } else {
            this.listConfig.selectionMode = SelectionMode.Single;
        }
        this.listConfig.numResultsPerPage = Number.MAX_VALUE;

        this.listConfig.items = this.screen.selectionList;
        this.listConfig.disabledItems = this.screen.selectionList.filter(e => !e.enabled);

        if (this.screen.defaultSelectItemIndex !== null && this.screen.defaultSelectItemIndex !== undefined) {
            this.listConfig.defaultSelectItemIndex = this.screen.defaultSelectItemIndex;
        }

        if (this.screen.numberItemsPerPage !== 0) {
            this.listConfig.numResultsPerPage = this.screen.numberItemsPerPage;
        }
    }

    ngAfterViewInit() {
        this.items.changes.subscribe(() => {
            console.log('changed');
        });
    }

    public onItemListChange(event: any[]): void {
        // this.selectedItems = event;
        // this.session.onAction("SelectedItemsChanged", this.selectedItems);
    }

    public onItemChange(event: any): void {
        this.index = this.screen.selectionList.indexOf(event);
        if (this.items) {
            this.scrollToItem();
        }
        if (this.screen.selectionChangedAction && this.index !== this.lastSelection) {
            this.lastSelection = this.index;
            this.session.onAction(this.screen.selectionChangedAction, this.index);
        }
    }

    public scrollToItem() {
        let indexToView = this.index;
        if (this.screen.numberItemsPerPage !== 0) {
            indexToView -= Math.trunc(this.index / this.screen.numberItemsPerPage) * this.screen.numberItemsPerPage;
        }
        if (this.items.toArray()[indexToView]) {
            this.items.toArray()[indexToView].nativeElement.scrollIntoView({ block: 'center' });
            // Smooth scrolling does not seem to work in Chrome dialog windows (IE: Resume transaction window).
            // Firefox will work fine though.
            //
            // this.items.toArray()[indexToView].nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.session.onAction(menuItem, this.index);
    }

    public keybindsEnabled(menuItem: IActionItem): boolean {
        return Configuration.enableKeybinds && menuItem.keybind && menuItem.keybind !== 'Enter';
    }
}
