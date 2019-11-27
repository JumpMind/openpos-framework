import { Component, Injector } from '@angular/core';
import { SaleItemCardListInterface } from './sale-item-card-list.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';


@ScreenPart({
  name: 'SaleItemCardList'
})
@Component({
  selector: 'app-sale-item-card-list',
  templateUrl: './sale-item-card-list.component.html',
  styleUrls: ['./sale-item-card-list.component.scss']
})
export class SaleItemCardListComponent extends ScreenPartComponent<SaleItemCardListInterface> {

  expandedIndex = 0;

  constructor(injector: Injector) {
    super(injector);
  }

  itemsTrackByFn(index, item: ISellItem) {
    return item.index;
  }

  screenDataUpdated() {
    this.expandedIndex = this.screenData.items.length - 1;
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

}
