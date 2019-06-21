import { ScreenPartComponent } from '../screen-part';
import { AfterViewInit, Component, OnInit, ViewChild, Input } from '@angular/core';
import { Validators, FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import { ValidatorsService } from '../../../core/services/validators.service';
import { MessageProvider } from '../../providers/message.provider';
import { PromptInterface } from '../../../screens-with-parts/prompt/prompt.interface';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { PromptFormPartInterface } from './prompt-form-part.interface';

@Component({
    selector: 'app-prompt-form-part',
    templateUrl: './prompt-form-part.component.html'
})
export class PromptFormPartComponent extends ScreenPartComponent<PromptFormPartInterface> implements AfterViewInit, OnInit {

    promptFormGroup: FormGroup;
    initialized = false;
    instructions: string;
    inputControlName = 'promptInputControl';
    hiddenInputControlName = 'promptInputHiddenDateControl';

    constructor(private validatorsService: ValidatorsService, messageProvider: MessageProvider) {
        super(messageProvider);
    }

    screenDataUpdated() {
        this.instructions = this.screenData.instructions;

        const group: any = {};
        const validators: ValidatorFn[] = [];
        validators.push(Validators.required);
        if (this.screenData.responseType) {
            validators.push(this.validatorsService.getValidator(this.screenData.responseType.toString()));
        }

        if (this.screenData.validators) {
            this.screenData.validators.forEach(v => validators.push(this.validatorsService.getValidator(v.toString())));
        }

        if (this.screenData.minLength) {
            validators.push(Validators.minLength(this.screenData.minLength));
        }

        if (this.screenData.maxLength) {
            validators.push(Validators.maxLength(this.screenData.maxLength));
        }

        if (this.screenData.validationPatterns) {
            for (const validationPattern of this.screenData.validationPatterns) {
                validators.push(Validators.pattern(validationPattern));
            }
        }

        if (this.screenData.max) {
            validators.push(Validators.max(this.screenData.max));
        }

        if (this.screenData.min) {
            validators.push(Validators.min(this.screenData.min));
        }

        group[this.inputControlName] = new FormControl(this.screenData.responseText, validators);
        // When showing a DATE, there is also a hidden field to handle picking of dates using
        // a date picker, need to add a FormControl for that also.
        if (this.screenData.responseType && this.screenData.responseType.toString() !== 'DatePartChooser' &&
            this.screenData.responseType.toString().toLowerCase().indexOf('date') >= 0) {
            group[this.hiddenInputControlName] = new FormControl();
        }
        this.promptFormGroup = new FormGroup(group);
    }

    ngAfterViewInit(): void {
        this.initialized = true;
    }

    onAction(menuItm: IActionItem) {
        this.sessionService.onAction(menuItm);
    }

    onFormSubmit(): void {
        if (this.promptFormGroup.valid) {
            const payload = this.promptFormGroup.value[this.inputControlName];
            if (this.screenData.actionButton) {
                this.sessionService.onAction(this.screenData.actionButton.action, payload);
            }
        }
    }

}
