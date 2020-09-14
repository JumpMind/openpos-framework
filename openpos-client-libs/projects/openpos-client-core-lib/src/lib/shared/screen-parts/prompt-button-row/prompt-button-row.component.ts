import { Component } from '@angular/core'; import { PromptButtonRowInterface } from './prompt-button-row.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';


@ScreenPart({
    name: 'promptButtonRow'
})
@Component({
    selector: 'app-prompt-button-row',
    templateUrl: './prompt-button-row.component.html',
    styleUrls: ['./prompt-button-row.component.scss']
})
export class PromptButtonRowComponent extends ScreenPartComponent<PromptButtonRowInterface> {

    screenDataUpdated() { }

}
