import { Component, ViewChild } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { PromptFormPartComponent } from '../../shared/screen-parts/prompt-form-part/prompt-form-part.component';
import { IOptionItem } from '../../core/interfaces/option-item.interface';
import { CustomerDisplayPromptInterface } from './customer-display-prompt.interface';

@ScreenComponent({
    name: 'CustomerDisplayPrompt'
})
@Component({
    selector: 'app-customer-display-prompt',
    templateUrl: './customer-display-prompt.component.html',
    styleUrls: ['./customer-display-prompt.component.scss']
})
export class CustomerDisplayPromptComponent extends PosScreen<CustomerDisplayPromptInterface> {

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
