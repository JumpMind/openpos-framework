import { Component, ElementRef, ViewChild, OnInit, Injector } from '@angular/core';
import { MobileSaleItemListInterface } from './mobile-sale-item-list.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { Observable } from 'rxjs';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { UIDataMessageService } from '../../../core/ui-data-message/ui-data-message.service';


@ScreenPart({
    name: 'MobileSaleItemList'
})
@Component({
    selector: 'app-mobile-sale-item-list',
    templateUrl: './mobile-sale-item-list.component.html',
    styleUrls: ['./mobile-sale-item-list.component.scss']
})
export class MobileSaleItemListComponent extends ScreenPartComponent<MobileSaleItemListInterface> implements OnInit {

    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;
    size = -1;
    items: Observable<ISellItem[]>;
    expandedIndex = 0;

    constructor(injector: Injector, private dataMessageService: UIDataMessageService) {
        super(injector);
    }

    screenDataUpdated() {
        this.items = this.dataMessageService.getData$(this.screenData.providerKey);
        this.items.subscribe(() => {
          this.items.forEach(i => {
            this.expandedIndex = i.length - 1;
          });
          this.scrollToBottom();
        });
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.scrollToBottom();
    }

    scrollToBottom(): void {
        try {
            this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
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
