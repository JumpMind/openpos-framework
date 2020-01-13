import { Component, Input } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Configuration } from '../../../configuration/configuration';
import { ImageTextPanelInterface } from './image-text-panel.interface';


@ScreenPart({
    name: 'imageTextPanel'
})
@Component({
    selector: 'app-image-text-panel',
    templateUrl: './image-text-panel.component.html',
    styleUrls: ['./image-text-panel.component.scss']
})
export class ImageTextPanelComponent extends ScreenPartComponent<ImageTextPanelInterface> {

    @Input() instructionsSize = 'text-md';

    screenDataUpdated() {
    }
}
