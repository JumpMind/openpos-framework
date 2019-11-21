import { Input, Component } from '@angular/core';

@Component({
    selector: 'app-title',
    templateUrl: './title.component.html'
})
export class TitleComponent {
    @Input() title: string;
    @Input() titleSize = 'text-lg';
}
