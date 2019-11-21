import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { OptionsInterface } from './options.interface';

@ScreenComponent({
    name: 'Options'
})
@Component({
    selector: 'app-options-screen',
    templateUrl: './options-screen.component.html',
    styleUrls: ['./options-screen.component.scss']
})
export class OptionsScreenComponent extends PosScreen<OptionsInterface> {

    buildScreen() {
    }

}
