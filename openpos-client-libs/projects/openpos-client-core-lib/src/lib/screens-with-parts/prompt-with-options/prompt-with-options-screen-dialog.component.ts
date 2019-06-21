import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { PromptWithOptionsScreenComponent } from './prompt-with-options-screen.component';


@DialogComponent({
    name: 'PromptWithOptions'
})
@Component({
    selector: 'app-prompt-with-options-screen-dialog',
    templateUrl: './prompt-with-options-screen-dialog.component.html'
})
export class PromptWithOptionsScreenDialogComponent extends PromptWithOptionsScreenComponent {
}
