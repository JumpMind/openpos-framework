import { PromptScreenComponent } from './prompt-screen.component';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';

@DialogComponent({
    name: 'Prompt'
})
@Component({
    selector: 'app-prompt-screen-dialog',
    templateUrl: './prompt-screen-dialog.component.html',
    styleUrls: ['./prompt-screen-dialog.component.scss']
})
export class PromptScreenDialogComponent extends PromptScreenComponent {
}
