import { Component, Injector, ElementRef, ViewChildren, QueryList, AfterViewInit } from '@angular/core';
import { SaleItemCardListInterface } from './sale-item-card-list.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { UIDataMessageService } from '../../../core/ui-data-message/ui-data-message.service';
import {merge, Observable} from 'rxjs';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';
import {takeUntil} from 'rxjs/operators';


@ScreenPart({
  name: 'SaleItemCardList'
})
@Component({
  selector: 'app-sale-item-card-list',
  templateUrl: './sale-item-card-list.component.html',
  styleUrls: ['./sale-item-card-list.component.scss']
})
export class SaleItemCardListComponent extends ScreenPartComponent<SaleItemCardListInterface> implements AfterViewInit {
  stop$: Observable<any>;
  expandedIndex = -1;
  numItems = 0;
  items$: Observable<ISellItem[]>;
  @ViewChildren('items', {read: ElementRef }) private itemsRef: QueryList<ElementRef>;

  constructor(injector: Injector, private dataMessageService: UIDataMessageService,
              protected keyPresses: KeyPressProvider) {
    super(injector);
    this.stop$ = merge(this.beforeScreenDataUpdated$, this.destroyed$);

    this.subscriptions.add(
      this.keyPresses.subscribe( 'ArrowDown', 1, (event: KeyboardEvent) => {
        // ignore repeats and check configuration
        if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
          return;
        }
        if ( event.type === 'keydown') {
          this.handleArrowKey(event);
        }
      })
    );

    this.subscriptions.add(
      this.keyPresses.subscribe( 'ArrowUp', 1, (event: KeyboardEvent) => {
        // ignore repeats and check configuration
        if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
          return;
        }
        if ( event.type === 'keydown') {
          this.handleArrowKey(event);
        }
      })
    );

  }

  itemsTrackByFn(index, item: ISellItem) {
    return item.index;
  }

  screenDataUpdated() {
    this.items$ = this.dataMessageService.getData$(this.screenData.providerKey);
    this.items$.subscribe(sellItems => {
      this.addSellItemsGlobalKeybinds(sellItems);

      this.items$.forEach(i => {
        this.numItems = i.length;
        this.expandedIndex = i.length - 1;
      });
      this.scrollToView(this.expandedIndex);
    });
  }

  ngAfterViewInit() {
    this.scrollToView(this.expandedIndex);
  }

  addSellItemsGlobalKeybinds(sellItems: ISellItem[]): void {
    const uniqueKeybinds = sellItems.reduce((allActions, sellItem) => {
        sellItem.menuItems.forEach(menuItem => allActions[menuItem.keybind] = menuItem);
        return allActions;
    }, {});

    const allActions = Object.keys(uniqueKeybinds).map(key => uniqueKeybinds[key]);

    this.keyPressProvider.globalSubscribe(allActions).pipe(
        takeUntil(this.stop$)
    ).subscribe(action => this.doAction(action, [this.expandedIndex]));
  }

  scrollToView(index: number): void {
    if (this.itemsRef) {
        const itemsRefArray = this.itemsRef.toArray();
        if (itemsRefArray && index >= 0 && index < itemsRefArray.length) {
            itemsRefArray[index].nativeElement.scrollIntoView({block: 'center'});
        }
    }
  }

  isItemExpanded(index: number): boolean {
    if (this.screenData.enableCollapsibleItems) {
      return index === this.expandedIndex;
    }
    return true;
  }

  updateExpandedIndex(index: number) {
    this.expandedIndex = index;
    this.scrollToView(this.expandedIndex);
  }

  handleArrowKey(event: KeyboardEvent) {
    let direction = 1;
    if (event.key === 'ArrowDown' || event.key === 'Tab') {
      direction = 1;
    } else if (event.key === 'ArrowUp') {
      direction = -1;
    } else {
      return;
    }

    let newIndex = this.expandedIndex + direction;

    if (this.expandedIndex === this.numItems - 1 && event.key === 'Tab') {
      newIndex = 0;
    }

    if (newIndex >= 0 && newIndex < this.numItems) {
        this.updateExpandedIndex(newIndex);
    }
  }

}
