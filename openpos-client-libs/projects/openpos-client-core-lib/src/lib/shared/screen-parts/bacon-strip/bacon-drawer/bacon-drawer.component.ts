import {Component, EventEmitter, OnDestroy, Output, Injector, Input} from '@angular/core';
import {IActionItem} from '../../../../core/actions/action-item.interface';
import {ScreenPart} from '../../../decorators/screen-part.decorator';
import {ScreenPartComponent} from '../../screen-part';
import {BaconStripInterface} from '../bacon-strip.interface';
import { KeyPressProvider } from '../../../providers/keypress.provider';
import { Configuration } from '../../../../configuration/configuration';

@ScreenPart({name: 'baconStrip'})
@Component({
  selector: 'app-bacon-drawer',
  templateUrl: './bacon-drawer.component.html',
  styleUrls: ['./bacon-drawer.component.scss']
})
export class BaconDrawerComponent extends ScreenPartComponent<BaconStripInterface> implements OnDestroy {

  @Output()
  buttonClicked = new EventEmitter();

  constructor(injector: Injector) {
    super(injector);
  }

  screenDataUpdated() {
  }

  buttonClick(action: IActionItem ) {
    this.buttonClicked.emit();
    super.doAction(action);
  }
}
