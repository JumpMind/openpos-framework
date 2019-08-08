import { ItemDetailInterface } from './item-detail.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

@ScreenComponent({
    name: 'ItemDetail'
})
@Component({
    selector: 'app-item-detail',
    templateUrl: './item-detail.component.html',
    styleUrls: ['./item-detail.component.scss'],
})
export class ItemDetailComponent extends PosScreen<ItemDetailInterface> {
    buildScreen() {
    }
}
