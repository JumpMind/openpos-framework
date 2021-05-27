import { Component } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import { StatusMessage } from '../status.message';
import { StatusService } from '../status.service';
import { PeripheralSelectionService, PeripheralCategory } from '../../../core/peripherals/peripheral-selection.service';
import { PeripheralSelectorComponent, PeripheralSelectorDialogData } from './selector/peripheral-selector.component';

@Component({
    templateUrl: './status-details.component.html',
    styleUrls: ['./status-details.component.scss']
})
export class StatusDetailsComponent {
    readonly status$: Observable<StatusMessage[]>;

    private _openSelectorDialog?: MatDialogRef<PeripheralSelectorComponent>;

    constructor(
        status: StatusService, 
        public peripheralSelection: PeripheralSelectionService,
        private dialog: MatDialog,
        private dialogRef: MatDialogRef<PeripheralSelectorComponent>
    ) {
        this.status$ = status.getStatus().pipe(
            map(s => Array.from(s.values()))
        );
    }

    onChangeSelectedPeripheral(category: PeripheralCategory) {
        this._openSelectorDialog = this.dialog.open(PeripheralSelectorComponent, {
            data: <PeripheralSelectorDialogData> {
                category: category
            },
            width: '75%'
        });

        this._openSelectorDialog.afterClosed().pipe(take(1)).subscribe(() => {
            this._openSelectorDialog = undefined;
        });
    }

    close() {
        if (this._openSelectorDialog) {
            this._openSelectorDialog.close();
        }

        this.dialogRef.close();
    }
}
