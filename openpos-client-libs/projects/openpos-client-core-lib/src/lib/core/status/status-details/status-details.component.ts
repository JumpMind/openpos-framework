
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SessionService } from '../../services/session.service';

import { StatusMessage } from '../status.message';
import { StatusService } from '../status.service';
import { PeriphealSelectionService, PeriphealCategory } from '../../peripheals/peripheral-selection.service';
import { PeripheralSelectorComponent, PeripheralSelectorDialogData } from './selector/peripheral-selector.component';

@Component({
    templateUrl: './status-details.component.html',
    styleUrls: ['./status-details.component.scss']
})
export class StatusDetailsComponent {
    readonly status$: Observable<StatusMessage[]>;

    constructor(
        status: StatusService, 
        public peripheralSelection: PeriphealSelectionService,
        private session: SessionService, 
        private dialog: MatDialog
    ) {
        this.status$ = status.getStatus().pipe(
            map(s => Array.from(s.values()))
        );
    }

    onChangeSelectedPeripheral(category: PeriphealCategory) {
        this.dialog.open(PeripheralSelectorComponent, {
            data: <PeripheralSelectorDialogData> {
                category: category
            }
        });
    }
}
