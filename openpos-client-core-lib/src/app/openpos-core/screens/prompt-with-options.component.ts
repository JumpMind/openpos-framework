import { FormGroup, FormControl, ValidatorFn, Validators } from '@angular/forms';
import { Component, OnInit, ViewChild } from '@angular/core';
import { SessionService } from '../services/session.service';
import { ChooseOptionsComponent } from './choose-options/choose-options.component';
import { PromptInputComponent } from '../common/controls/prompt-input.component';

import { PhoneUSValidatorDirective } from '../common/validators/phone.directive';
import { OpenPosValidators } from '../common/validators/openpos-validators';
import { ValidatorsService } from '../services/validators.service';
import { IMenuItem } from '..';

@Component({
  selector: 'app-prompt-with-options',
  templateUrl: './prompt-with-options.component.html'
})
export class PromptWithOptionsComponent extends ChooseOptionsComponent implements OnInit {

  actionButton: IMenuItem;
  promptFormGroup: FormGroup;

  constructor(public session: SessionService, private validatorsService: ValidatorsService) {
    super(session);
  }

  public ngOnInit(): void {

    const group: any = {};
    const validators: ValidatorFn[] = [];
    validators.push(Validators.required);
    validators.push(this.validatorsService.getValidator(this.screen.responseType));

    group['promptInputControl'] = new FormControl(this.screen.responseText, validators);

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
    this.onAction(this.actionButton.action);
  }

}
