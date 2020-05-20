import {Component, EventEmitter, Injector, OnDestroy, OnInit, Output} from '@angular/core';
import {IActionItem} from '../../../../core/actions/action-item.interface';
import {ScreenPart} from '../../../decorators/screen-part.decorator';
import {ScreenPartComponent} from '../../screen-part';
import {BaconStripInterface} from '../bacon-strip.interface';
import {KeyPressProvider} from '../../../providers/keypress.provider';
import {Configuration} from '../../../../configuration/configuration';
import {merge} from 'rxjs';

@ScreenPart({name: 'baconStrip'})
@Component({
  selector: 'app-bacon-drawer',
  templateUrl: './bacon-drawer.component.html',
  styleUrls: ['./bacon-drawer.component.scss']
})
export class BaconDrawerComponent extends ScreenPartComponent<BaconStripInterface> implements OnInit, OnDestroy {
  @Output()
  buttonClicked = new EventEmitter();
  keyPressProvider: KeyPressProvider;
  stop$ = merge(this.beforeScreenDataUpdated$, this.destroyed$);
  keybindsEnabled: boolean;

  constructor(injector: Injector) {
    super(injector);
    this.keyPressProvider = injector.get(KeyPressProvider);
  }

  screenDataUpdated() {
    this.keybindsEnabled = Configuration.enableKeybinds;

    if(this.screenData.actions) {
      this.screenData.actions.forEach(action => {
        // Give these keys low priority so that keybindings inside the screen can take priority
        this.keyPressProvider.subscribe(action.keybind, 90, () => super.doAction(action), this.stop$);
      });
    }
  }

  buttonClick(action: IActionItem ) {
    this.buttonClicked.emit();
    super.doAction(action);
  }
}
