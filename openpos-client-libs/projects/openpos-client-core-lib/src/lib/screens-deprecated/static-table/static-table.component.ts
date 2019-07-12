import { Component } from '@angular/core';
import { DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable } from 'rxjs';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { SelectionMode } from '../choose-options/choose-options-screen.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'Table'
  })
@Component({
    selector: 'app-static-table',
    templateUrl: './static-table.component.html'
})
export class StaticTableComponent extends PosScreen<any> {

    rowData: RowDatabase;
    dataSource: RowDataSource | null;

    /** Table columns */
    columns: Array<ColumnDef> = [];
    columnIds: Array<string> = [];
    columnsById: { [key: string]: ColumnDef } = {};

    selectionMode: SelectionMode;
    selectedRow: number;

    /** Prompt text to display to user */
    text: string;

    constructor() {
        super();
    }

    private initColumnDefs(): void {
        this.columns = [];
        this.columnIds = [];
        this.columnsById = {};
        // Create the list of column definitions
        let columnIdx = 0;
        if (this.screen.headerLabels) {
            // Initialize the list of column metadata and other convenience
            // column data structures
            this.screen.headerLabels.forEach(
                (headerLabel) => {
                    this.columns.push(
                        { index: columnIdx++, columnId: headerLabel, headerLabel: headerLabel }
                    );
                }
            );
            this.columnIds = this.columns.map(c => c.columnId);

            // If our selection mode allows for selection of a Single table row
            // or Multiple table rows, add an extra '_selection' column to the front
            // of the list of other columnIds. The _selection column
            // will be used by the template to enable selection of a row (or rows).
            if ([SelectionMode.Single, SelectionMode.Multiple].indexOf(this.selectionMode) >= 0) {
                this.columnIds.unshift('_selection');
            }

            this.columns.forEach((col) => { this.columnsById[col.columnId] = col; });
        }
    }

    isRowSelected(rowIndex: number) {
        return this.selectedRow === rowIndex;
    }

    buildScreen() {
        this.selectionMode = SelectionMode[this.screen.selectionMode as string];
        this.initColumnDefs();
        this.rowData = new RowDatabase(this.screen.tableData);

        this.dataSource = new RowDataSource(this.rowData);
        this.text = this.screen.text;
        this.selectedRow = this.screen.selectedRow;
    }

    onSelectRow(rowIndex: number) {
        this.selectedRow = rowIndex;
    }

    onAction(action: string) {
        if (action) {
            this.session.onAction(action, this.selectedRow);
        }
    }
}

/**
 * Attributes that describe a column.
 */
export interface ColumnDef {
    /** Index of the column, 0-based */
    index: number;
    /** An identifier for the column, can just be the column header label */
    columnId: string;
    /** A label for the column that can be displayed to the user */
    headerLabel: string;

}

/**
 * Attributes that describe a row in the table to be displayed
 */
export interface TableRow {
    /** Index of the row, 0-based */
    index: number;
    /** Pre-formatted cell values to display to the user. The count of values
     * for each row must be >= the number of columns. */
    values: Array<string>;
}

/**
 * Holds the static data to be displayed. Data is provided in the form of
 * an Array of String Arrays.
 */
export class RowDatabase {
    /** Stream that emits an event whenever the data has been modified. */
    dataChange: BehaviorSubject<TableRow[]> = new BehaviorSubject<TableRow[]>([]);
    get data(): TableRow[] { return this.dataChange.value; }

    constructor(rows: string[][]) {
        rows.forEach((row) => {
            this.addRow(row);
        });
    }

    /** Adds a new row to the database. */
    addRow(row: Array<string>) {
        const copiedData = this.data.slice();
        copiedData.push(this.createNewRow(row));
        this.dataChange.next(copiedData);
    }

    private createNewRow(row: Array<string>): TableRow {
        return {
            index: this.data.length,
            values: row
        };
    }
}

export class RowDataSource extends DataSource<any> {
    constructor(private _rowDatabase: RowDatabase) {
        super();
    }

    /** Connect function called by the table to retrieve one stream containing the data to render. */
    connect(): Observable<TableRow[]> {
        return this._rowDatabase.dataChange;
    }

    disconnect() { }
}
