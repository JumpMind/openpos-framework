import {Component, Input} from '@angular/core';
import {ICustomerDetails} from "../../../screens-with-parts/customer-search-result-dialog/customer-search-result-dialog.interface";
import {Observable} from "rxjs";
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";

@Component({
    selector: 'app-display-customer-lookup',
    templateUrl: './display-customer-lookup.component.html',
    styleUrls: ['./display-customer-lookup.component.scss']
})
export class DisplayCustomerLookupComponent {
    @Input() customer: ICustomerDetails;

    isMobile: Observable<boolean>;

    constructor(media: OpenposMediaService) {
        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }
}