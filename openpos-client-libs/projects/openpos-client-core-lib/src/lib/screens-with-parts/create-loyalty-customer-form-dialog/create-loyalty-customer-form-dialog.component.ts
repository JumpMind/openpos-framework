import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import {Component, Injector} from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import {CreateLoyaltyCustomerFormInterface} from "./create-loyalty-customer-form.interface";
import {MediaBreakpoints, OpenposMediaService} from "../../core/media/openpos-media.service";
import {Observable} from "rxjs";

@DialogComponent({
    name: 'CreateLoyaltyCustomerDialog',
})
@Component({
    selector: 'app-create-loyalty-customer-form-dialog',
    templateUrl: './create-loyalty-customer-form-dialog.component.html',
    styleUrls: [ './create-loyalty-customer-form-dialog.component.scss']
})
export class CreateLoyaltyFormDialogComponent extends PosScreen<CreateLoyaltyCustomerFormInterface> {

    isMobile: Observable<boolean>;
    constructor(injector: Injector, private media: OpenposMediaService) {
        super(injector);
        this.initIsMobile();
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