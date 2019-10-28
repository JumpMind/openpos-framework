import { Component, Injector } from '@angular/core';
import { MobileTotalsPartInterface } from './mobile-totals-part.interface';
import { ScreenPartComponent } from '../screen-part';
import { ScreenPart } from '../../decorators/screen-part.decorator';


@ScreenPart({
    name: 'MobileTotalsPart'
})
@Component({
    selector: 'app-mobile-totals-part',
    templateUrl: './mobile-totals-part.component.html',
    styleUrls: ['./mobile-totals-part.component.scss']
})
export class MobileTotalsPartComponent extends ScreenPartComponent<MobileTotalsPartInterface> {

    constructor(private injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
    }

}
