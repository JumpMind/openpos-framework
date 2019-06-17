import { Component } from '@angular/core';
import { ProgressBarPartInterface } from './progress-bar-part.interface';
import { ScreenPartComponent } from '../screen-part';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { MessageProvider } from '../../providers/message.provider';


@ScreenPart({
    name: 'progressBar'
})
@Component({
    selector: 'app-progress-bar-part',
    templateUrl: './progress-bar-part.component.html',
    styleUrls: ['./progress-bar-part.component.scss']
})
export class ProgressBarPartComponent extends ScreenPartComponent<ProgressBarPartInterface> {

    constructor(messageProvider: MessageProvider) {
        super(messageProvider);
    }

    buildScreen() { }

    screenDataUpdated() { }

}
