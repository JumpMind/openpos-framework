import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { IDetailTextScreen } from './detail-text-screen.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'DetailText'
})
@Component({
  selector: 'app-detail-text-screen',
  templateUrl: './detail-text-screen.component.html',
  styleUrls: ['./detail-text-screen.component.scss']
})
export class DetailTextScreenComponent extends PosScreen<IDetailTextScreen> {

  buildScreen() {}

}
