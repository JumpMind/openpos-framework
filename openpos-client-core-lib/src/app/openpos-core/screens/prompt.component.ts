import { FormGroup, FormControl, ValidatorFn, Validators } from '@angular/forms';
import { PromptInputComponent } from './../common/controls/prompt-input.component';
import { IScreen } from '../common/iscreen';
import { Component, ViewChild, AfterViewInit, DoCheck, OnInit } from '@angular/core';
import { SessionService } from '../services/session.service';
import { PhoneUSValidatorDirective } from '../common/validators/phone.directive';
import { OpenPosValidators } from '../common/validators/openpos-validators';
import { ValidatorsService } from '../services/validators.service';

@Component({
  selector: 'app-prompt',
  templateUrl: './prompt.component.html'
})
export class PromptComponent implements AfterViewInit, IScreen, OnInit {
  initialized = false;
  promptFormGroup: FormGroup;
  screen: any;

  constructor(public session: SessionService, private validatorsService: ValidatorsService) {
  }

  show(screen: any) {
    this.screen = screen;
  }

  ngOnInit(): void {

    const group: any = {};
    const validators: ValidatorFn[] = [];
    validators.push(Validators.required);
    validators.push(this.validatorsService.getValidator(this.screen.responseType));

    if (this.screen.minLength) {
      validators.push(Validators.minLength(this.screen.minLength));
    }

    if (this.screen.maxLength) {
      validators.push(Validators.maxLength(this.screen.maxLength));
    }

    group['promptInputControl'] = new FormControl(this.screen.responseText, validators);
    // When showing a DATE, there is also a hidden field to handle picking of dates using
    // a date picker, need to add a FormControl for that also.
    if (this.screen.responseType === 'DATE') {
      group['promptInputHiddenDateControl'] = new FormControl();
    }
    this.promptFormGroup = new FormGroup(group);
  }

  ngAfterViewInit(): void {
    this.initialized = true;
  }

  onFormSubmit(): void {
    if (this.promptFormGroup.valid) {
      const payload = this.promptFormGroup.value['promptInputControl'];
      if (this.screen.actionButton) {
        this.session.onAction(this.screen.actionButton.action, payload);
      } else {
        this.session.onAction(this.screen.action, payload);
      }
    }
  }

}
