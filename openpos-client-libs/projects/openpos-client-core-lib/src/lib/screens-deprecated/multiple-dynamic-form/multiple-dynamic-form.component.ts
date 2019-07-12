import { Component } from '@angular/core';
import { IMultipleDynamicFormScreen } from './multiple-dynamic-form-screen.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';


@Component({
    selector: 'app-multiple-dynamic-form',
    templateUrl: './multiple-dynamic-form.component.html'
})
export class MultipleDynamicFormComponent extends PosScreen<IMultipleDynamicFormScreen> {
    screen: IMultipleDynamicFormScreen;

    buildScreen(): void {
    }
}
