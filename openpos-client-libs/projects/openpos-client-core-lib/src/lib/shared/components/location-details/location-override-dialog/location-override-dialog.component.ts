import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ILocationData } from 'projects/openpos-client-core-lib/src/lib/core/location-providers/location-data.interface';

@Component({
    templateUrl: './location-override-dialog.component.html'
})
export class LocationOverrideDialogComponent {

    overrideForm: FormGroup;
    availableCountries: string[];
    overridePrompt: string;
    enableOverride = false;

    constructor(public fb: FormBuilder, public dialogRef: MatDialogRef<LocationOverrideDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any) {
        this.overrideForm = this.fb.group({
            postalCode: [null],
            country: [null],
        });
        this.availableCountries = data.countries;
        this.overridePrompt = data.overridePrompt;
        this.enableOverride = data.overrideEnabled;
    }

    updateOverride() {
        this.enableOverride = !this.enableOverride;
    }

    areFieldsSet(): boolean {
        return this.overrideForm.get('postalCode').value && this.overrideForm.get('country').value;
    }

    onClose() {
        this.dialogRef.close(null);
    }

    onEnter() {
        this.dialogRef.close({
            type: 'manual',
            postalCode: this.overrideForm.get('postalCode').value,
            country: this.overrideForm.get('country').value
        } as ILocationData);
    }

}
