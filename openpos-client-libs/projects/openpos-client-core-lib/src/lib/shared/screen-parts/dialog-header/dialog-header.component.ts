import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Component, Injector } from '@angular/core';
import { DialogHeaderInterface } from './dialog-header.interface';
import { ScreenPartComponent } from '../screen-part';

@ScreenPart({
    name: 'dialogHeader'})
@Component({
    selector: 'app-dialog-header',
    templateUrl: './dialog-header.component.html',
    styleUrls: ['./dialog-header.component.scss']
})
export class DialogHeaderComponent extends ScreenPartComponent<DialogHeaderInterface> {

    constructor( injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
        if (this.screenData.backButton) {
            this.screenData.backButton.keybind = 'Escape';
        }
    }
}
