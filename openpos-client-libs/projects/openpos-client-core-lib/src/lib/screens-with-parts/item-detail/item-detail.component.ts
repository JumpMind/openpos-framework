import { PosScreen } from '../pos-screen.component';
import { ItemDetailInterface } from './item-detail.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { Component } from '@angular/core';

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
