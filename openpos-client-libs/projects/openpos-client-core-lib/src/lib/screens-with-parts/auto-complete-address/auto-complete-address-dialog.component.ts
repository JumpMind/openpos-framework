import { Component } from '@angular/core';
import {DialogComponent} from '../../shared/decorators/dialog-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';

@DialogComponent({
    name: 'AutoCompleteAddress'
})
@Component({
    selector: 'app-auto-complete-address-dialog',
    templateUrl: './auto-complete-address-dialog.component.html',
    styleUrls: ['./auto-complete-address-dialog.component.scss']
})
export class AutoCompleteAddressDialogComponent extends PosScreen<any> {

    buildScreen() { }

}
