import { Component, Input } from '@angular/core';
import { KebabButtonComponent } from '../kebab-button/kebab-button.component';

@Component({
    selector: 'app-kebab-label-button',
    templateUrl: './kebab-label-button.component.html',
    styleUrls: ['./kebab-label-button.component.scss']
})
export class KebabLabelButtonComponent extends KebabButtonComponent {

    @Input()
    label: string;

    public onMenuItemClick($event) {
        this.menuItemClick.emit($event);
    }

}
