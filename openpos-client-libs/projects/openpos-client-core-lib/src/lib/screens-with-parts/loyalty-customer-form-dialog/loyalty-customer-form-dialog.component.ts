import {Component, Injector} from '@angular/core';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import {LoyaltyCustomerFormInterface} from "./loyalty-customer-form.interface";
import {Observable} from "rxjs/internal/Observable";
import {MediaBreakpoints, OpenposMediaService} from "../../core/media/openpos-media.service";

@DialogComponent({
    name: 'LoyaltyCustomerDialog',
})
@Component({
    selector: 'app-loyalty-customer-form-dialog',
    templateUrl: './loyalty-customer-form-dialog.component.html',
    styleUrls: [ './loyalty-customer-form-dialog.component.scss']
})
export class LoyaltyCustomerFormDialogComponent extends PosScreen<LoyaltyCustomerFormInterface> {

    isMobile: Observable<boolean>;
    constructor(injector: Injector, private media: OpenposMediaService) {
        super(injector);
        this.initIsMobile();
        console.log('[ =====  LoyaltyCustomerFormDialogComponent  ===== ]');
        console.dir(this);
        console.dir(this.screen);
        console.dir(screen);
    }

    initIsMobile(): void {
        this.isMobile = this.media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    buildScreen() { }

}