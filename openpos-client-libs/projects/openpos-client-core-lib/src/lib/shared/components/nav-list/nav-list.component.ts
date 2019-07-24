import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Logger } from '../../../core/services/logger.service';
import { SessionService } from '../../../core/services/session.service';
import { ActionService } from '../../../core/actions/action.service';

@Component({
    selector: 'app-nav-list',
    templateUrl: './nav-list.component.html',
    styleUrls: ['./nav-list.component.scss']
})
export class NavListComponent {

    optionItems: Array<IActionItem>;

    constructor(    private log: Logger,
                    @Inject(MAT_DIALOG_DATA) public data: any,
                    public dialogRef: MatDialogRef<NavListComponent>,
                    protected actionService: ActionService) {
        log.info(data);
        this.optionItems = data.optionItems;
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.log.info('do menu ' + this.data.payload);
        this.actionService.doAction(menuItem, this.data.payload ? this.data.payload : null);
        this.dialogRef.close();
    }

}
