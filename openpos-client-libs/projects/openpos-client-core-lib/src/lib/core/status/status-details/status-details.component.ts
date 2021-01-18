import { Component } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StatusMessage } from '../status.message';
import { StatusService } from '../status.service';
import { PeripheralSelectionService, PeripheralCategory } from '../../peripherals/peripheral-selection.service';
import { PeripheralSelectorComponent, PeripheralSelectorDialogData } from './selector/peripheral-selector.component';

@Component({
    templateUrl: './status-details.component.html',
    styleUrls: ['./status-details.component.scss']
})
export class StatusDetailsComponent {
    readonly status$: Observable<StatusMessage[]>;

    constructor(
        status: StatusService, 
        public peripheralSelection: PeripheralSelectionService,
        private dialog: MatDialog
    ) {
        this.status$ = status.getStatus().pipe(
            map(s => Array.from(s.values()))
        );
    }

    onChangeSelectedPeripheral(category: PeripheralCategory) {
        this.dialog.open(PeripheralSelectorComponent, {
            data: <PeripheralSelectorDialogData> {
                category: category
            }
        });
    }
}
