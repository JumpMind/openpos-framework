import {Component, Injector} from '@angular/core';
import {DialogComponent} from '../../shared/decorators/dialog-component.decorator';
import {PosScreen} from '../pos-screen/pos-screen.component';
import {ErrorDialogInterface} from "./error-dialog.interface";

@DialogComponent({
    name: 'ErrorDialog'
})
@Component({
    selector: 'app-error-dialog',
    templateUrl: './error-dialog.component.html',
    styleUrls: ['./error-dialog.component.scss']
})
export class ErrorDialogComponent extends PosScreen<ErrorDialogInterface> {

    constructor(injector: Injector) {
        super(injector);
    }

    buildScreen() {
    }




}
