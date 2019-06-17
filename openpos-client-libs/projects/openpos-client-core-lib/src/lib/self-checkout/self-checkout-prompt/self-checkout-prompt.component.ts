
import { Component, ViewChild } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { IOptionItem } from '../../screens-deprecated/choose-options/option-item.interface';
import { SelfCheckoutPromptInterface } from './self-checkout-prompt.interface';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { PromptFormPartComponent } from '../../shared/screen-parts/prompt-form-part/prompt-form-part.component';


@ScreenComponent({
    name: 'SelfCheckoutPrompt'
})
@Component({
    selector: 'app-self-checkout-prompt',
    templateUrl: './self-checkout-prompt.component.html',
    styleUrls: ['./self-checkout-prompt.component.scss']
})
export class SelfCheckoutPromptComponent extends PosScreen<SelfCheckoutPromptInterface> {

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
