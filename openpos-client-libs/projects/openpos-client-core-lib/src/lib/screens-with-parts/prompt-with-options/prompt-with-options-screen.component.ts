
import { Component, ViewChild } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { IOptionItem } from '../../core/interfaces/option-item.interface';
import { PromptWithOptionsInterface } from './prompt-with-options.interface';
import { PosScreen } from '../pos-screen/pos-screen.component';
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
            this.doAction(action, this.promptForm.promptFormGroup.value[this.promptForm.inputControlName]);
        }
    }
}
