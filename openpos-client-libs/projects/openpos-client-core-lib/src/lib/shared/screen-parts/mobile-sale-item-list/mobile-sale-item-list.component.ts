import { Component, ElementRef, ViewChild, AfterViewChecked, OnInit } from '@angular/core';
import { MobileSaleItemListInterface } from './mobile-sale-item-list.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';


@ScreenPart({
    name: 'MobileSaleItemList'
})
@Component({
    selector: 'app-mobile-sale-item-list',
    templateUrl: './mobile-sale-item-list.component.html',
    styleUrls: ['./mobile-sale-item-list.component.scss']
})
export class MobileSaleItemListComponent extends ScreenPartComponent<MobileSaleItemListInterface> implements OnInit, AfterViewChecked {

    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;
    size = -1;

    screenDataUpdated() {
    }

    ngAfterViewChecked() {
        if (this.screenData.items && this.size !== this.screenData.items.length) {
            this.scrollToBottom();
            this.size = this.screenData.items.length;
        }
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

}
