import { Component, Injector, ElementRef } from '@angular/core';
import { SaleItemCardListInterface } from './sale-item-card-list.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { UIDataMessageService } from '../../../core/ui-data-message/ui-data-message.service';
import { Observable } from 'rxjs';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';


@ScreenPart({
  name: 'SaleItemCardList'
})
@Component({
  selector: 'app-sale-item-card-list',
  templateUrl: './sale-item-card-list.component.html',
  styleUrls: ['./sale-item-card-list.component.scss']
})
export class SaleItemCardListComponent extends ScreenPartComponent<SaleItemCardListInterface> {

  expandedIndex = -1;
  numItems = 0;
  items: Observable<ISellItem[]>;

  constructor(injector: Injector, private dataMessageService: UIDataMessageService, private elementRef: ElementRef,
              protected keyPresses: KeyPressProvider) {
    super(injector);
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
    this.items = this.dataMessageService.getData$(this.screenData.providerKey);
    this.items.subscribe(() => {
      this.items.forEach(i => {
        this.numItems = i.length;
        this.expandedIndex = i.length - 1;
      });
      this.scrollToBottom();
    });
  }

  scrollToBottom(): void {
    try {
        this.elementRef.nativeElement.scrollTop = this.elementRef.nativeElement.scrollHeight;
    } catch (err) { }
  }

  isItemExpanded(index: number): boolean {
    if (this.screenData.enableCollapsibleItems) {
      return index === this.expandedIndex;
    }
    return true;
  }

  updateExpandedIndex(index: number) {
    this.expandedIndex = index;
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
