import { Component, Injector } from '@angular/core';
import { ProgressBarPartInterface } from './progress-bar-part.interface';
import { ScreenPartComponent } from '../screen-part';
import { ScreenPart } from '../../decorators/screen-part.decorator';


@ScreenPart({
    name: 'progressBar'
})
@Component({
    selector: 'app-progress-bar-part',
    templateUrl: './progress-bar-part.component.html',
    styleUrls: ['./progress-bar-part.component.scss']
})
export class ProgressBarPartComponent extends ScreenPartComponent<ProgressBarPartInterface> {

    constructor(injector: Injector) {
        super(injector);
    }

    buildScreen() { }

    screenDataUpdated() { }

}
