import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PromptInterface } from './prompt.interface';

@ScreenComponent({
    name: 'Prompt'
})
@Component({
    selector: 'app-prompt-screen',
    templateUrl: './prompt-screen.component.html',
    styleUrls: ['./prompt-screen.component.scss']
})
export class PromptScreenComponent extends PosScreen<PromptInterface> {
    buildScreen() {
    }
}
