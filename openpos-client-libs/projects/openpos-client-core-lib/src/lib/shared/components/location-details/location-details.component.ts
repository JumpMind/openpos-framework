import { Component, Input } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { LocationService } from '../../../core/services/location.service';
import { MatDialogRef, MatDialog } from '@angular/material';
import { LocationOverrideDialogComponent } from './location-override-dialog/location-override-dialog.component';

@Component({
    selector: 'app-location-details',
    templateUrl: './location-details.component.html',
    styleUrls: ['./location-details.component.scss']
})

export class LocationDetailsComponent {

    @Input()
    locationOverridePrompt: string;

    postalCode: string;
    private overrideDialogRef: MatDialogRef<LocationOverrideDialogComponent>;

    constructor(public session: SessionService, private locationService: LocationService, private matDialog: MatDialog) {
        locationService.getPostalCode().subscribe(p => {
            this.postalCode = p;
        });
    }

    hasOverridePrompt(): boolean {
        return this.locationOverridePrompt !== undefined && this.locationOverridePrompt !== null;
    }

    onClick() {
        if (this.hasOverridePrompt()) {
            this.overrideDialogRef = this.matDialog.open(LocationOverrideDialogComponent, {
                disableClose: true,
                width: '50%',
                height: '50%',
                data: {
                    countries: this.locationService.getAvailableCountries(),
                    overridePrompt: this.locationOverridePrompt,
                    overrideEnabled: this.locationService.hasManualOverride()
                }
            });

            this.overrideDialogRef.afterClosed().subscribe( result => {
                if (result) {
                    this.locationService.setLocationData(result);
                }
            });
        }
    }
}
