import { PromptWithInfoScreenComponent } from './prompt-with-info-screen.component';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';

@DialogComponent({
    name: 'PromptWithInfo'
})
@Component({
    selector: 'app-prompt-with-info-screen-dialog',
    templateUrl: './prompt-with-info-screen-dialog.component.html',
    styleUrls: ['./prompt-with-info-screen-dialog.component.scss']
})
export class PromptWithInfoScreenDialogComponent extends PromptWithInfoScreenComponent {
}
