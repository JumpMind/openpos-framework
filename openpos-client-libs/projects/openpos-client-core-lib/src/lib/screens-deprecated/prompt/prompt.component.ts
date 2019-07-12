import { IActionItem } from '../../core/interfaces/action-item.interface';
import { FormGroup, FormControl, ValidatorFn, Validators } from '@angular/forms';
import { Component, AfterViewInit } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { ValidatorsService } from '../../core/services/validators.service';

/**
 * @ignore
 */
@Component({
    selector: 'app-prompt',
    templateUrl: './prompt.component.html'
})
export class PromptComponent extends PosScreen<any> implements AfterViewInit {
    initialized = false;
    promptFormGroup: FormGroup;
    instructions: string;

    constructor(private validatorsService: ValidatorsService) {
        super();
    }

    buildScreen() {
        this.instructions = this.screen.instructions;

        const group: any = {};
        const validators: ValidatorFn[] = [];
        validators.push(Validators.required);
        validators.push(this.validatorsService.getValidator(this.screen.responseType));

        if (this.screen.validators) {
            this.screen.validators.forEach(v => validators.push(this.validatorsService.getValidator(v)));
        }

        if (this.screen.minLength) {
            validators.push(Validators.minLength(this.screen.minLength));
        }

        if (this.screen.maxLength) {
            validators.push(Validators.maxLength(this.screen.maxLength));
        }

        if (this.screen.pattern) {
            validators.push(Validators.pattern(this.screen.pattern));
        }

        group['promptInputControl'] = new FormControl(this.screen.responseText, validators);
        // When showing a DATE, there is also a hidden field to handle picking of dates using
        // a date picker, need to add a FormControl for that also.
        if (this.screen.responseType && this.screen.responseType !== 'DatePartChooser' && this.screen.responseType.toLowerCase().indexOf('date') >= 0) {
            group['promptInputHiddenDateControl'] = new FormControl();
        }
        this.promptFormGroup = new FormGroup(group);
    }

    ngAfterViewInit(): void {
        this.initialized = true;
    }

    onAction(menuItm: IActionItem) {
        this.session.onAction(menuItm);
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
