import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";

import { PeripheralSelectionService, PeripheralCategory, PeripheralDevice } from '../../../peripherals/peripheral-selection.service';

export interface PeripheralSelectorDialogData {
    category: PeripheralCategory
}

@Component({
    templateUrl: './peripheral-selector.component.html',
    styleUrls: ['./peripheral-selector.component.scss']
})
export class PeripheralSelectorComponent {
    constructor(
        @Inject(MAT_DIALOG_DATA) public data: PeripheralSelectorDialogData,
        public periph: PeripheralSelectionService
    ) {
    }

    assignDevice(device: PeripheralDevice) {
        this.periph.selectDevice(this.data.category, device);
    }
}
