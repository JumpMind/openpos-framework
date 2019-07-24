import { ViewChildren, AfterViewInit, Input, QueryList, ViewChild, Component, Injector } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ScreenPartComponent } from '../screen-part';
import { FormBuilder } from '../../../core/services/form-builder.service';
import { DynamicFormFieldComponent } from '../../components/dynamic-form-field/dynamic-form-field.component';
import { ShowErrorsComponent } from '../../components/show-errors/show-errors.component';
import { IForm } from '../../../core/interfaces/form.interface';
import { IFormElement } from '../../../core/interfaces/form-field.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

@Component({
    selector: 'app-dynamic-form-part',
    templateUrl: './dynamic-form-part.component.html',
    styleUrls: ['./dynamic-form-part.component.scss']
})
export class DynamicFormPartComponent extends ScreenPartComponent<IForm> {

    @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;
    @ViewChild('formErrors') formErrors: ShowErrorsComponent;
    form: FormGroup;

    buttons: IFormElement[];

    private _alternateSubmitActions: string[];

    @Input() set formName(name: string) {
        this.screenPartName = name;
    }

    @Input() submitButton: IActionItem;

    constructor(private formBuilder: FormBuilder, injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
        this.buttons = new Array<IFormElement>();

        this.form = this.formBuilder.group(this.screenData);

        if (this.screenData && this.screenData.formElements) {
            this.screenData.formElements.forEach(element => {
                if (element.elementType === 'Button') {
                    this.buttons.push(element);
                }
            });
        }
    }

    @Input()
    get alternateSubmitActions(): string[] {
        return this._alternateSubmitActions;
    }

    set alternateSubmitActions(actions: string[]) {
        this._alternateSubmitActions = actions;
        if (actions) {
            actions.forEach(action => {

                this.actionService.registerActionPayload(action, () => {
                    if (this.form.valid) {
                        this.formBuilder.buildFormPayload(this.form, this.screenData);
                        return this.screenData;
                    } else {
                        // Show errors for each of the fields where necessary
                        Object.keys(this.form.controls).forEach(f => {
                            const control = this.form.get(f);
                            control.markAsTouched({ onlySelf: true });
                        });
                        throw Error('form is invalid');
                    }
                });
            });
        }
    }

    submitForm() {
        this.formBuilder.buildFormPayload(this.form, this.screenData);
        this.doAction(this.submitButton, this.screenData);
    }

    onFieldChanged(formElement: IFormElement) {
        if (formElement.valueChangedAction) {
            this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.doAction(formElement.valueChangedAction, this.screenData);
        }
    }
}

