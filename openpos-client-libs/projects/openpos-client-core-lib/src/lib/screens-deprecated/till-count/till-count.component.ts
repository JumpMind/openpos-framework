import { DynamicFormControlComponent } from './../../shared/components/dynamic-form-control/dynamic-form-control.component';
import { Component, ViewChild } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { IFormElement } from '../../core/interfaces/form-field.interface';

/**
 * @ignore
 */
@Component({
  selector: 'app-till-count',
  templateUrl: './till-count.component.html',
  styleUrls: ['./till-count.component.scss']
})
export class TillCountComponent extends PosScreen<any> {

  nextAction: IActionItem;
  public form: IForm;
  @ViewChild('tillForm') tillForm: DynamicFormControlComponent;

  constructor() {
      super();
  }

  buildScreen() {
    // After screen is initialized, all we need to do is
    // get an updated total from the server.  This saves
    // unnecessary rebuilding of the screen
    this.form = this.screen.form;
    this.nextAction = this.screen.nextAction;
    this.screen.total = this.screen.total;
  }

  onFieldChanged(eventData: {formElement: IFormElement, event: Event}) {
    this.form = this.screen.form;
    this.session.onAction('formChanged', this.form);
  }

  onNextAction() {
    if (this.tillForm.form.valid) {
        this.session.onAction(this.nextAction.action, this.screen.form);
    }
  }

}
