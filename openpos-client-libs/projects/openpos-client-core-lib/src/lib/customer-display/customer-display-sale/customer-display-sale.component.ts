import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component, OnInit, AfterViewChecked, ViewChild, ElementRef, Injector } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { Observable } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../core/media/openpos-media.service';

@ScreenComponent({
    name: 'CustomerDisplaySale'
})
@Component({
    selector: 'app-customer-display-sale',
    templateUrl: './customer-display-sale.component.html',
    styleUrls: ['./customer-display-sale.component.scss']

})
export class CustomerDisplaySaleComponent extends PosScreen<any> implements OnInit, AfterViewChecked {
    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;

    isMobile: Observable<boolean>;

    public size = -1;

    public items: ISellItem[];

    constructor(injector: Injector, media: OpenposMediaService) {
        super(injector);
        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, false],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    buildScreen() {
        this.items = this.screen.items;
    }

    ngAfterViewChecked() {
        if (this.items && this.size !== this.items.length) {
            this.scrollToBottom();
            this.size = this.items.length;
        }
    }

    scrollToBottom(): void {
        try {
            this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
        } catch (err) { }
    }

    ngOnInit(): void {
        this.scrollToBottom();
    }
}
