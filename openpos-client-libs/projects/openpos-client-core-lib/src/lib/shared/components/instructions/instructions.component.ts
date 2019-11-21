import { Input, Component } from '@angular/core';

@Component({
    selector: 'app-instructions',
    templateUrl: './instructions.component.html'
})
export class InstructionsComponent {
    @Input() instructions: string;
    @Input() instructionsSize = 'text-md';
}
