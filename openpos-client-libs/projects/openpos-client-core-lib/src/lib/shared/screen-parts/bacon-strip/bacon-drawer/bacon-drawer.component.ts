import {Component, EventEmitter, OnDestroy, Output, Injector} from '@angular/core';
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

  actions: IActionItem[];
  selectedIndex = -1;

  constructor(injector: Injector, protected keyPresses: KeyPressProvider) {
    super(injector);

    this.subscriptions.add(
      this.keyPresses.subscribe( 'ArrowDown', 1, (event: KeyboardEvent) => {
        // ignore repeats and check configuration
        if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
          return;
        }
        if ( event.type === 'keydown') {
          this.handleArrowKey(event);
        }
      })
    );

    this.subscriptions.add(
      this.keyPresses.subscribe( 'ArrowUp', 1, (event: KeyboardEvent) => {
        // ignore repeats and check configuration
        if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
          return;
        }
        if ( event.type === 'keydown') {
          this.handleArrowKey(event);
        }
      })
    );

    this.subscriptions.add(
      this.keyPresses.subscribe('Tab', 1, (event: KeyboardEvent) => {
        if (event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
          return;
        }
        if ( event.type === 'keydown') {
          event.preventDefault();
          this.handleArrowKey(event);
        }
      })
    );

    this.subscriptions.add(
      this.keyPresses.subscribe('Enter', 1, (event: KeyboardEvent) => {
        if (event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
          return;
        }
        if ( event.type === 'keydown') {
          this.selectOption();
        }
      })
    );
  }

  screenDataUpdated() {
    this.actions = this.screenData.actions;
  }

  buttonClick(action: IActionItem ) {
    this.buttonClicked.emit();
    super.doAction(action);
  }

  selectOption() {
    if (this.selectedIndex >= 0 && this.selectedIndex < this.actions.length) {
        this.buttonClick(this.actions[this.selectedIndex]);
    }
  }

  handleArrowKey(event: KeyboardEvent) {
    let direction = 1;
    if (event.key === 'ArrowDown' || event.key === 'Tab') {
      direction = 1;
    } else if (event.key === 'ArrowUp') {
      direction = -1;
    } else {
      return;
    }

    let newIndex = this.selectedIndex + direction;
    if (this.selectedIndex === this.actions.length - 1 && event.key === 'Tab') {
      newIndex = 0;
    }

    while (newIndex > 0 && newIndex < this.actions.length - 1 && !this.actions[newIndex].enabled) {
      newIndex = newIndex + direction;
    }

    if ( this.actions && newIndex >= 0 && newIndex < this.actions.length && this.actions[newIndex]) {
        this.selectedIndex = newIndex;
    }
  }
}
