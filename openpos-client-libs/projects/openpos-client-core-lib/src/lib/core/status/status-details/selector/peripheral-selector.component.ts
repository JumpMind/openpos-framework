import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";

import { PeriphealSelectionService, PeriphealCategory, PeriphealDevice } from '../../../peripheals/peripheral-selection.service';

export interface PeripheralSelectorDialogData {
    category: PeriphealCategory
}

@Component({
    templateUrl: './peripheral-selector.component.html',
    styleUrls: ['./peripheral-selector.component.scss']
})
export class PeripheralSelectorComponent {
    constructor(
        @Inject(MAT_DIALOG_DATA) public data: PeripheralSelectorDialogData,
        public periph: PeriphealSelectionService
    ) {
    }

    assignDevice(device: PeriphealDevice) {
        this.periph.selectDevice(this.data.category, device);
    }
}
