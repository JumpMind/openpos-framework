import { Component, ViewChildren, AfterViewInit, Input, QueryList, ViewChild } from '@angular/core';
import { FormGroup, AbstractControl } from '@angular/forms';
import { DynamicFormFieldComponent } from '../dynamic-form-field/dynamic-form-field.component';
import { ShowErrorsComponent } from '../show-errors/show-errors.component';
import { IFormElement } from '../../../core/interfaces/form-field.interface';
import { IForm } from '../../../core/interfaces/form.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ScreenService } from '../../../core/services/screen.service';
import { FormBuilder } from '../../../core/services/form-builder.service';
import { ActionService } from '../../../core/actions/action.service';

@Component({
  selector: 'app-dynamic-form-control',
  templateUrl: './dynamic-form-control.component.html',
  styleUrls: ['./dynamic-form-control.component.scss']
})
export class DynamicFormControlComponent implements AfterViewInit {

  @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;
  @ViewChild('formErrors') formErrors: ShowErrorsComponent;
  form: FormGroup;

  buttons: IFormElement[];

  private _screenForm: IForm;
  private _alternateSubmitActions: string[];
  @Input() submitButton: IActionItem;

  constructor(
      public screenService: ScreenService,
      private formBuilder: FormBuilder,
      private actionService: ActionService) {}

  @Input()
  get screenForm(): IForm {
    return this._screenForm;
  }

  set screenForm( screenForm: IForm) {
    this._screenForm = screenForm;
    this.buttons = new Array<IFormElement>();

    this.form = this.formBuilder.group(screenForm);

    screenForm.formElements.forEach(element => {
        if (element.elementType === 'Button') {
        this.buttons.push(element);
        }
    });

  }

  @Input()
  get alternateSubmitActions(): string[] {
    return this._alternateSubmitActions;
  }

  set alternateSubmitActions( actions: string[] ) {
    this._alternateSubmitActions = actions;
    if (actions) {
      actions.forEach(action => {

        this.actionService.registerActionPayload(action, () => {
          if (this.form.valid) {
            this.formBuilder.buildFormPayload(this.form, this._screenForm);
            return this._screenForm;
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


  ngAfterViewInit() {
    // Delays less than 1 sec do not work correctly.
    this.display(1000);
  }

  public display(delay: number) {
    const nonReadonlyChildren = this.children.filter( child => {
         return child.isReadOnly() === false;
      });

      if ( nonReadonlyChildren.length > 0 ) {
        setTimeout(() => nonReadonlyChildren[0].focus(), delay);
      }
  }

  submitForm() {
    if (this.form.valid) {
      this.formBuilder.buildFormPayload(this.form, this._screenForm);
      this.actionService.doAction(this.submitButton, this._screenForm);
    } else {
        // Set focus on the first invalid field found
        const invalidFieldKey = Object.keys(this.form.controls).find(key => {
            const ctrl: AbstractControl = this.form.get(key);
            return ctrl.invalid && ctrl.dirty;
        });
        if (invalidFieldKey) {
            const invalidField = this.children.find(f => f.controlName === invalidFieldKey).field;
            if (invalidField) {
                const invalidElement = document.getElementById(invalidFieldKey);
                if (invalidElement) {
                    invalidElement.scrollIntoView();
                } else {
                    invalidField.focus();
                }
            }
        } else {
            if (this.formErrors.shouldShowErrors()) {
                const formErrorList = this.formErrors.listOfErrors();
                if (formErrorList && formErrorList.length > 0) {
                    document.getElementById('formErrorsWrapper').scrollIntoView();
                }
            }
        }
    }
  }

  onFieldChanged(formElement: IFormElement) {
    if (formElement.valueChangedAction) {
        this.formBuilder.buildFormPayload(this.form, this._screenForm);
        this.actionService.doAction({action: formElement.valueChangedAction}, this._screenForm );
    }
  }

  onButtonClick(formElement: IFormElement) {
    const actionItem = { action: formElement.action, confirmationDialog: formElement.confirmationDialog };
    this.actionService.doAction(actionItem, null);
  }
}

