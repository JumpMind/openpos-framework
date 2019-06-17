import { Component } from '@angular/core';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PromptInterface } from './prompt.interface';

@ScreenComponent({
    name: 'Prompt'
})
@Component({
    selector: 'app-prompt-screen',
    templateUrl: './prompt-screen.component.html'
})
export class PromptScreenComponent extends PosScreen<PromptInterface> {
    buildScreen() {
    }
}
