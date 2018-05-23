import { ITextMask, TextMask } from './../../textmask';
import { IMenuItem } from '../../imenuitem';
import { IScreen } from '../../iscreen';
import { Component, ViewChildren, AfterViewInit, DoCheck, OnInit, Output, Input, EventEmitter, QueryList } from '@angular/core';
import { SessionService } from '../../../services/session.service';
import { MatSelectChange } from '@angular/material';
import {
  FormArray, FormBuilder, FormGroup, Validators, AbstractControl,
  FormControl, NgForm, ValidatorFn, NG_VALIDATORS
} from '@angular/forms';
import { IFormElement } from '../../iformfield';
import { Observable } from 'rxjs/Observable';
import { ScreenService } from '../../../services/screen.service';
import { OpenPosValidators } from '../../validators/openpos-validators';
import { ValidatorsService } from '../../../services/validators.service';
import { IForm } from '../../iform';
import { MatInput } from '@angular/material';
import { DynamicFormFieldComponent } from '../dynamic-form-field/dynamic-form-field.component';

@Component({
  selector: 'app-dynamic-form-control',
  templateUrl: './dynamic-form-control.component.html',
  styleUrls: ['./dynamic-form-control.component.scss']
})
export class DynamicFormControlComponent implements AfterViewInit {
  
  @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;

  ngAfterViewInit() {
    const nonReadonlyChildren = this.children.filter( child => {
      if(child.field){
        return child.field.readonly === false;
      }
      return false;      
    });

    if( nonReadonlyChildren.length > 0 ) {
      setTimeout(() => nonReadonlyChildren[0].field.focus(), 0);
    }
       
  }
  
  @Input() 
  get screenForm(): IForm{
    return this._screenForm;
  }

  set screenForm( screenForm: IForm){
    this._screenForm = screenForm;
    const group: any = {};

    this.buttons = new Array<IFormElement>();

    screenForm.formElements.forEach(element => {

      const ctlValidators: ValidatorFn[] = this.createControlValidators(element);
      group[element.id] = new FormControl(element.value, ctlValidators);
      // For a DATE type element, there is also a hidden field to handle picking of dates using
      // a date picker, need to add a FormControl for that hidden input also.
      if (element.inputType === 'Date' || element.inputType === 'NoYearDate') {
        group[element.id + 'Hidden'] = new FormControl();
      }

      if (element.elementType === 'Button') {
        this.buttons.push(element);
      }
    });

    const grpValidators: ValidatorFn[] = this.createFormLevelValidators();
    this.form = new FormGroup(group, grpValidators);
  }

  @Input() submitAction: string;

  @Input() submitButtonText = 'Next';

  @Input() 
  get screen(): any{
    return this._screen;
  }

  set screen( screen: any ){
    this._screen = screen;
    if (screen.alternateSubmitActions) {
      screen.alternateSubmitActions.forEach(action => {

        this.session.registerActionPayload(action, () => {
          if (this.form.valid) {
            this.buildFormPayload();
            return this.session.response = this._screenForm;
          } else {
            // Show errors for each of the fields where necessary
            Object.keys(this.form.controls).forEach(f => {
              const control = this.form.get(f);
              control.markAsTouched({onlySelf: true});
            });
            throw Error('form is invalid');
          }
        });
      });
    }
  }

  form: FormGroup;

  buttons: IFormElement[];

  private _screenForm: IForm;
  private _screen: IScreen;

  constructor(public session: SessionService, public screenService: ScreenService, private validatorService: ValidatorsService) {}

  ngOnInit() {


  }

  /**
   * Since an individual validator cannot be added after construction, this method
   * provides a way to add extra validators onto those already provided by the form.
   * A list of validators is returned which include the provided list of extraValidators.
   * The returned list of validators can then be set on the form. See the setValidators method
   * on the FormGroup class.
   *
   * @param extraValidators Optional additional validators to be added to the form.
   */
  createFormLevelValidators(extraValidators: ValidatorFn[] = []): ValidatorFn[] {
    let validators: ValidatorFn[] = [];
    if (this.screenForm.requiresAtLeastOneValue) {
      validators.push(OpenPosValidators.RequireAtleastOne);
    }

    validators = validators.concat(extraValidators);
    return validators;
  }

  /**
   * Since an individual validator cannot be added after construction, this method
   * provides a way to add extra validators onto those already normally assigned to the
   * IFormElement.
   * A list of validators is returned which include the provided list of extraValidators.
   * The returned list of validators can then be set on the form. See the setValidators method
   * on the FormComponent class.
   *
   * @param extraValidators Optional additional validators to be added to the form.
   */
  createControlValidators(element: IFormElement, extraValidators: ValidatorFn[] = []): ValidatorFn[] {
    let validators: ValidatorFn[] = [];
    if (element.required) {
      validators.push(Validators.required);
    }

    if (element.pattern) {
      validators.push(Validators.pattern(element.pattern));
    }

    if (element.minLength) {
      validators.push(Validators.minLength(element.minLength));
    }

    if (element.maxLength) {
      validators.push(Validators.maxLength(element.maxLength));
    }

    validators.push(this.validatorService.getValidator(element.inputType));

    validators = validators.concat(extraValidators);
    return validators;
  }

  submitForm() {
    if (this.form.valid) {

      this.buildFormPayload();
      this.session.onAction(this.submitAction, this._screenForm);
    }
  }

  onFieldChanged(formElement: IFormElement) {
    if (formElement.valueChangedAction) {
      this.buildFormPayload();
      this.session.onValueChange(formElement.valueChangedAction, this._screenForm );
    }
  }

  onButtonClick(formElement: IFormElement) {
    this.session.onAction(formElement.buttonAction);
  }

  private buildFormPayload() {
    this._screenForm.formElements.forEach(element => {
      if (element.hasOwnProperty('value')) {
        element.value = this.form.value[element.id];
      }
      if(element.hasOwnProperty('checked')) {
        element.checked = this.form.value[element.id];
      }
    });
  }
}

