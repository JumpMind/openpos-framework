import { Component, Output, EventEmitter } from '@angular/core';
import { OptionsListInterface } from './options-list.interface';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';


@ScreenPart({
    name: 'optionsList'
})
@Component({
    selector: 'app-options-list',
    templateUrl: './options-list.component.html',
    styleUrls: ['./options-list.component.scss']
})
export class OptionsListComponent extends ScreenPartComponent<OptionsListInterface> {

    @Output()
    optionClick = new EventEmitter<IActionItem>();

    screenDataUpdated() { }

    buildScreen() { }

    onOptionClick(actionItem: IActionItem): void {
        this.optionClick.emit(actionItem);
    }

}
