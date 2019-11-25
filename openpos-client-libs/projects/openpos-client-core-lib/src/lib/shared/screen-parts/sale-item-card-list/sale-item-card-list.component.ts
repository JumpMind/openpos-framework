import { Component, Injector, ElementRef } from '@angular/core';
import { SaleItemCardListInterface } from './sale-item-card-list.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { UIDataMessageService } from '../../../core/ui-data-message/ui-data-message.service';
import { Observable } from 'rxjs';
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

  expandedIndex = -1;
  items: Observable<ISellItem[]>;

  constructor(injector: Injector, private dataMessageService: UIDataMessageService, private elementRef: ElementRef) {
    super(injector);
  }

  screenDataUpdated() {
    this.items = this.dataMessageService.getData$(this.screenData.providerKey);
    this.items.forEach(i => this.expandedIndex = this.expandedIndex++);
    this.scrollToBottom();
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

}
