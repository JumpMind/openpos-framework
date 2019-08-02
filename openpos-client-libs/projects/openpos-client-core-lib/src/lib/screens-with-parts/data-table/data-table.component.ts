import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

@ScreenComponent({
    name: 'DataTable'
})
@Component({
    selector: 'app-data-table',
    templateUrl: './data-table.component.html',
    styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent extends PosScreen<any> {

    rows = [];
    columnHeaders = [];

    buildScreen() {
        if (this.screen.rows) {
            this.rows = this.screen.rows;
        }
        if (this.screen.columnHeaders) {
            this.columnHeaders = this.screen.columnHeaders;
        }
    }

}
