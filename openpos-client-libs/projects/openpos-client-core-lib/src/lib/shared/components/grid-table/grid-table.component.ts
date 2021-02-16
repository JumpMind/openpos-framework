import { Component, OnInit, Input } from '@angular/core';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';

@Component({
    selector: 'app-grid-table',
    templateUrl: './grid-table.component.html',
    styleUrls: ['./grid-table.component.scss']
})
export class GridTableComponent implements OnInit {

    @Input()
    columnHeaders = [];

    @Input()
    rows = [];

    outerStyle: object;

    constructor(public actionService: ActionService) {
    }

    ngOnInit(): void {
        const numberRows = this.rows.length;
        const numberCols = this.columnHeaders.length;

        this.outerStyle = {
            'grid-template-rows':
                `repeat(${numberRows}, min-content)`,
            'grid-template-columns':
                `repeat(${numberCols}, minmax(min-content, auto))`,
        };
    }

    isAction(line: any): boolean {
        return line instanceof Object && line.hasOwnProperty('action');
    }

    public doAction(action: IActionItem) {
        this.actionService.doAction(action);
      }

}
