import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Component, Injector, ViewChild } from '@angular/core';
import { DialogHeaderInterface } from './dialog-header.interface';
import { MessageProvider } from '../../providers/message.provider';
import { ScreenPartComponent } from '../screen-part';
import { IconButtonComponent } from '../../components/icon-button/icon-button.component'

@ScreenPart({
    name: 'dialogHeader'})
@Component({
    selector: 'app-dialog-header',
    templateUrl: './dialog-header.component.html',
    styleUrls: ['./dialog-header.component.scss']
})
export class DialogHeaderComponent extends ScreenPartComponent<DialogHeaderInterface> {

    temporaryDisable = 0;

    constructor( injector: Injector) {
        super(injector);
    }

    @ViewChild(IconButtonComponent) backButton;

    ngAfterViewChecked() {
        if (this.backButton && this.temporaryDisable > 10 && this.temporaryDisable < 20) {
            this.backButton.allowTabbing(true);
            this.temporaryDisable = 30;
        } else {
            this.temporaryDisable++;
        }
    }

    screenDataUpdated() {
        if (this.screenData.backButton) {
            this.screenData.backButton.keybind = 'Escape';
        }
    }
}
