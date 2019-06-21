import { Component, Input } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/interfaces/action-item.interface';

@Component({
    selector: 'app-location-details',
    templateUrl: './location-details.component.html',
    styleUrls: ['./location-details.component.scss']
})

export class LocationDetailsComponent {
    @Input() postalCode: string;
    @Input() locationButton?: IActionItem;

    constructor(public session: SessionService) { }

    onClick() {
        if (this.locationButton) {
            this.session.onAction(this.locationButton);
        }
    }
}
