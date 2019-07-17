import { Component, AfterViewInit, ViewChildren, QueryList, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { MessageProvider } from '../../providers/message.provider';
import { FormGroup } from '@angular/forms';
import { FormBuilder } from '../../../core/services/form-builder.service';
import { IFormElement } from '../../../core/interfaces/form-field.interface';
import { DynamicFormFieldComponent } from '../../components/dynamic-form-field/dynamic-form-field.component';
import { IFormOptionItem } from './form-option-item.interface';

@ScreenPart({
    name: 'options'
})
@Component({
  selector: 'app-choose-options-part',
  templateUrl: './choose-options-part.component.html',
  styleUrls: ['./choose-options-part.component.scss']
})
export class ChooseOptionsPartComponent extends ScreenPartComponent<IFormOptionItem[]> implements AfterViewInit {
    options: IFormOptionItem[];
    formGroups: FormGroup[];

    @ViewChildren(DynamicFormFieldComponent) fields: QueryList<DynamicFormFieldComponent>;

    public showOptions = true;
    public selectedOption: IFormOptionItem;
    public selectedForm: FormGroup;
    constructor(injector: Injector, private formBuilder: FormBuilder ) {
        super(injector);
     }

    screenDataUpdated() {
        if ( !this.screenData ) {
            return;
        }
        this.options = this.screenData;
        this.formGroups = [];
        this.options.forEach( f => {
            this.formGroups.push(this.formBuilder.group(f.form));
        });
    }

    ngAfterViewInit(): void {
        this.fields.changes.subscribe(() => {
            if (this.fields.first) {
                this.fields.first.focus();
            }
        });
    }

    onMakeOptionSelection( formOption: IFormOptionItem, formGroup: FormGroup): void {
        if (formOption.form.formElements.length > 0) {
            this.selectedOption = formOption;
            this.selectedForm = formGroup;
            this.showOptions = false;
        } else {
            this.doAction(formOption.optionAction);
        }
    }

    onBackButtonPressed(): void {
        this.showOptions = true;
    }

    onFieldChanged(formElement: IFormElement, option: IFormOptionItem, group: FormGroup) {
        if (formElement.valueChangedAction) {
            this.formBuilder.buildFormPayload(group, option.form);
            this.doAction({action: formElement.valueChangedAction, doNotBlockForResponse: true}, this.screenData);
        }
    }

    onSubmitForm( option: IFormOptionItem, group: FormGroup): void {
        this.doAction(option.optionAction, this.formBuilder.buildFormPayload(group, option.form));
    }
}
