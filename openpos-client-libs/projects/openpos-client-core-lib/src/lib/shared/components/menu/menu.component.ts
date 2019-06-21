import { Component, Input, EventEmitter, Output } from '@angular/core';
import { IActionItem } from '../../../core/interfaces/action-item.interface';

@Component({
    selector: 'app-menu',
    templateUrl: './menu.component.html',
    styleUrls: ['./menu.component.scss'],
})
export class MenuComponent {

    @Input() menuItems: IActionItem[];
    @Output() menuItemClicked = new EventEmitter<IActionItem>();
}
