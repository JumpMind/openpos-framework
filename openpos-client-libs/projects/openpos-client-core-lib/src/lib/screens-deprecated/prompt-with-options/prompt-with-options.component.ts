import { FormGroup, FormControl, ValidatorFn, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { ChooseOptionsComponent } from '../choose-options/choose-options.component';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { ValidatorsService } from '../../core/services/validators.service';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'PromptWithOptions'
})
@Component({
  selector: 'app-prompt-with-options',
  templateUrl: './prompt-with-options.component.html',
  styleUrls: ['./prompt-with-options.component.scss']
})
export class PromptWithOptionsComponent extends ChooseOptionsComponent implements OnInit {

  actionButton: IActionItem;
  promptFormGroup: FormGroup;

  constructor( private validatorsService: ValidatorsService) {
    super();
  }

  public ngOnInit(): void {

    const group: any = {};
    const validators: ValidatorFn[] = [];
    validators.push(Validators.required);
    validators.push(this.validatorsService.getValidator(this.screen.responseType));
    let value;

    if (this.screen.responseType === 'ONOFF') {
      if (!this.screen.responseText) {
          value = 'false';
      }
      // from server responseText is string , we are sending back as boolean
      // ideally toggle control would return ON/OFF
      value = (this.screen.responseText === 'ON') ? true : false;
      if (value) {
        this.screen.responseText = 'ON';
      } else {
        this.screen.responseText = 'OFF';
      }
    } else {
      value = this.screen.responseText;
  }

    group['promptInputControl'] = new FormControl(value, validators);

    if (this.screen.showComments) {
      group['comments'] = new FormControl(this.screen.comments, Validators.required);
    }
    this.promptFormGroup = new FormGroup(group);
    this.actionButton = this.screen.actionButton;
  }

  onAction(action: string): void {
    if (this.promptFormGroup.valid) {
      if (this.screen.showComments) {
        this.session.onAction(action,
          {
            response: this.promptFormGroup.value['promptInputControl'],
            comment: this.promptFormGroup.value['comments']
          });
      } else {
        this.session.onAction(action, this.promptFormGroup.value['promptInputControl']);
      }
    }
  }

  onFormSubmit(): void {
    if (this.actionButton) {
      this.onAction(this.actionButton.action);
    }
  }

}
