import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { DataTableComponent } from './data-table.component';

@DialogComponent({
    name: 'DataTable'
})
@Component({
    selector: 'app-data-table-dialog',
    templateUrl: './data-table-dialog.component.html',
    styleUrls: ['./data-table-dialog.component.scss']
})
export class DataTableDialogComponent extends DataTableComponent {
}
