import { SelectionListScreenComponent } from './selection-list-screen.component';
import { Component } from '@angular/core';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';

@DialogComponent({
    name: 'SelectionList'
})
@Component({
    selector: 'app-selection-list-screen-dialog',
    templateUrl: './selection-list-screen-dialog.component.html',
    styleUrls: ['./selection-list-screen.component.scss']
})
export class SelectionListScreenDialogComponent extends SelectionListScreenComponent {
}
