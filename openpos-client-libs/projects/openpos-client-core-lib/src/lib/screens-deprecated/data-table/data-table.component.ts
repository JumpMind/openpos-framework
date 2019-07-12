import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';

/**
 * @ignore
 */
@Component({
    selector: 'app-data-table',
    templateUrl: './data-table.component.html',
    styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent extends PosScreen<any> {

    rows = [];
    columnHeaders = [];

    constructor() {
        super();
    }

    buildScreen() {
        if (this.screen.rows) {
            this.rows = this.screen.rows;
        }
        if (this.screen.columnHeaders) {
            this.columnHeaders = this.screen.columnHeaders;
        }
    }

}
