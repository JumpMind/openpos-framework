import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';

@Component({
    selector: 'app-nav-list',
    templateUrl: './nav-list.component.html',
    styleUrls: ['./nav-list.component.scss']
})
export class NavListComponent {

    optionItems: Array<IActionItem>;

    constructor(
                    @Inject(MAT_DIALOG_DATA) public data: any,
                    public dialogRef: MatDialogRef<NavListComponent>,
                    protected actionService: ActionService) {
        console.info(data);
        this.optionItems = data.optionItems;
    }

    public doMenuItemAction(menuItem: IActionItem) {
        console.info('do menu ' + this.data.payload);
        this.actionService.doAction(menuItem, this.data.payload ? this.data.payload : null);
        this.dialogRef.close();
    }

}
