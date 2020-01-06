import { Component, Input } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Configuration } from '../../../configuration/configuration';
import { ImagePanelInterface } from './image-panel.interface';


@ScreenPart({
    name: 'imagePanel'
})
@Component({
    selector: 'app-image-panel',
    templateUrl: './image-panel.component.html',
    styleUrls: ['./image-panel.component.scss']
})
export class ImagePanelComponent extends ScreenPartComponent<ImagePanelInterface> {

    @Input() instructionsSize = 'text-md';

    screenDataUpdated() {
    }
}
