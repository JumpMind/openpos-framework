import {Component, EventEmitter, OnDestroy, Output} from '@angular/core';
import {IActionItem} from '../../../../core/actions/action-item.interface';
import {ScreenPart} from '../../../decorators/screen-part.decorator';
import {ScreenPartComponent} from '../../screen-part';
import {BaconStripInterface} from '../bacon-strip.interface';

@ScreenPart({name: 'baconStrip'})
@Component({
  selector: 'app-bacon-drawer',
  templateUrl: './bacon-drawer.component.html',
  styleUrls: ['./bacon-drawer.component.scss']
})
export class BaconDrawerComponent extends ScreenPartComponent<BaconStripInterface> implements OnDestroy{

  @Output()
  buttonClicked = new EventEmitter();


  screenDataUpdated() {
  }

  buttonClick(action: IActionItem ) {
    this.buttonClicked.emit();
    super.doAction(action);
  }
}
