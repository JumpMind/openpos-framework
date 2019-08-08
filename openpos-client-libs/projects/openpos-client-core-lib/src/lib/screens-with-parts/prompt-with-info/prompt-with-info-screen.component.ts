import { Component } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { PromptWithInfoInterface } from './prompt-with-info.interface';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'PromptWithInfo'
})
@Component({
  selector: 'app-prompt-with-info-screen',
  templateUrl: './prompt-with-info-screen.component.html'
})
export class PromptWithInfoScreenComponent extends PosScreen<PromptWithInfoInterface> {

  buildScreen() {
  }
}
