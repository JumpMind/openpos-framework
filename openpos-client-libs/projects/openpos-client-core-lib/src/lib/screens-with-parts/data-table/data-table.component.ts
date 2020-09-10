import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { IActionItem } from '../../core/actions/action-item.interface';
import { Configuration } from '../../configuration/configuration';
import { DataTableInterface } from './data-table.interface';

@ScreenComponent({
    name: 'DataTable'
})
@Component({
    selector: 'app-data-table',
    templateUrl: './data-table.component.html',
    styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent extends PosScreen<DataTableInterface> {

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

    public keybindsEnabled(menuItem: IActionItem): boolean {
        return Configuration.enableKeybinds && menuItem.keybind && menuItem.keybind !== 'Enter';
    }

}
