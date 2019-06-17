
import { Component, AfterViewInit, OnInit, ViewChild } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PromptScreenComponent } from '../prompt/prompt-screen.component';
import { ValidatorsService } from '../../core/services/validators.service';
import { IOptionItem } from '../../screens-deprecated/choose-options/option-item.interface';
import { PromptWithOptionsInterface } from './prompt-with-options.interface';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { FormGroup, ValidatorFn, Validators, FormControl } from '@angular/forms';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { PromptFormPartComponent } from '../../shared/screen-parts/prompt-form-part/prompt-form-part.component';


@ScreenComponent({
    name: 'PromptWithOptions'
})
@Component({
    selector: 'app-prompt-with-options-screen',
    templateUrl: './prompt-with-options-screen.component.html'
})
export class PromptWithOptionsScreenComponent extends PosScreen<PromptWithOptionsInterface> {

    @ViewChild(PromptFormPartComponent) private promptForm: PromptFormPartComponent;
    public optionItems: IOptionItem[];

    buildScreen() {
        this.optionItems = this.screen.options;
    }

    onOptionSelected(action: string) {
        if (this.promptForm.promptFormGroup.valid) {
            this.session.onAction(action, this.promptForm.promptFormGroup.value[this.promptForm.inputControlName]);
        }
    }
}
