import { ItemDetailInterface } from './item-detail.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import {Component, Injector, Optional} from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import {MediaBreakpoints, OpenposMediaService} from "../../core/media/openpos-media.service";
import {Observable} from "rxjs";

@ScreenComponent({
    name: 'ItemDetail'
})
@Component({
    selector: 'app-item-detail',
    templateUrl: './item-detail.component.html',
    styleUrls: ['./item-detail.component.scss'],
})
export class ItemDetailComponent extends PosScreen<ItemDetailInterface> {
    isMobile: Observable<boolean>;
    carouselSize: String;
    constructor( @Optional() injector: Injector, media: OpenposMediaService) {
        super(injector);
        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));

        this.isMobile.subscribe(mobile => {
            if (mobile) {
                this.carouselSize = 'sm';
            } else {
                this.carouselSize = 'md';
            }
        });
    }

    buildScreen() {
    }
}
