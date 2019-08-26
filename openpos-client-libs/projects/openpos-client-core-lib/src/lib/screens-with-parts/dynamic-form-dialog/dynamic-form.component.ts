import { Component } from '@angular/core';
import { DynamicFormInterface } from './dynamic-form.interface';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

@ScreenComponent({
    name: 'DynamicForm',
})
@Component({
  selector: 'app-dynamic-form',
  templateUrl: './dynamic-form.component.html',
  styleUrls: [ './dynamic-form.component.scss']
})
export class DynamicFormComponent extends PosScreen<DynamicFormInterface> {

    buildScreen() {
    }

}
