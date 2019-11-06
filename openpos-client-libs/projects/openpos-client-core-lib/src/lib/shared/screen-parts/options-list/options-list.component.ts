import { Component } from '@angular/core';
import { OptionsListInterface } from './options-list.interface';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';



@ScreenPart({
    name: 'optionsList'
})
@Component({
    selector: 'app-options-list',
    templateUrl: './options-list.component.html',
    styleUrls: ['./options-list.component.scss']
})
export class OptionsListComponent extends ScreenPartComponent<OptionsListInterface> {

    screenDataUpdated() { }

    buildScreen() { }

}
