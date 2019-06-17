import { Component } from '@angular/core';
import { PromptComponent } from '../prompt/prompt.component';
import { ValidatorsService } from '../../core/services/validators.service';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'PromptWithInfo'
})
@Component({
  selector: 'app-prompt-with-info',
  templateUrl: './prompt-with-info.component.html',
  styleUrls: ['./prompt-with-info.component.scss']
})
export class PromptWithInfoComponent extends PromptComponent {

  constructor( private validators: ValidatorsService) {
    super( validators);
  }


}
