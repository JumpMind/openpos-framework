import { Component, Output, EventEmitter } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { IForm } from '../../core/interfaces/form.interface';
import { IFormElement } from '../../core/interfaces/form-field.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';

/**
 * @ignore
 */
@Component({
  selector: 'app-form',
  templateUrl: './form.component.html'
})
export class FormComponent extends PosScreen<any> {

  public form: IForm;
  formButtons: IFormElement[];
  @Output() onFieldChanged = new EventEmitter<{formElement: IFormElement, event: Event}>();

  buildScreen() {
    this.form = this.screen.form;
    this.formButtons = this.screen.form.formElements.filter((e) => e.elementType === 'Button');
  }

  onFormElementChanged(formElement: IFormElement, event: Event): void {
    this.onFieldChanged.emit({formElement: formElement, event: event});
  }

  onEnter(value: string) {
    // If there is a button which is a submitButton, submit the form
    // with that button's action
    const submitButtons: IFormElement[] = this.form.formElements.filter(
      elem => elem.elementType === 'Button' && elem.submitButton);

    if (submitButtons.length > 0) {
      this.session.onAction(submitButtons[0].action, this.form);
    }
  }

  onItemAction(menuItem: IActionItem, $event): void {
    this.session.onAction(menuItem, this.form);
  }

  onButtonAction(action: string) {
    this.session.onAction(action, this.form);
  }

  onSubmitOptionSelected(formElement: IFormElement, valueIndex: number, event: Event) {
    if (formElement.selectedIndexes) {
      formElement.selectedIndexes = [valueIndex];
    }
    this.session.onAction(formElement.id, this.form);
  }

  getPlaceholderText(formElement: IFormElement) {
    let text = '';
    if (formElement.label) {
      text += formElement.label;
    }
    if (text && formElement.placeholder) {
      text = `${text} - ${formElement.placeholder}`;
    }

    return text;
  }
}
