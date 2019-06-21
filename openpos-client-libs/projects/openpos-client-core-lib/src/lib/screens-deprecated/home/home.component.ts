import { Component, OnInit } from '@angular/core';
import { ObservableMedia} from '@angular/flex-layout';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { Configuration } from '../../configuration/configuration';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'Home'
})
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: [ './home.component.scss']
})
export class HomeComponent extends PosScreen<any> implements OnInit {

  screen: any;
  public menuItems: IActionItem[];
  gutterSize = 40;
  gridColumns = 3;

  constructor( public media: ObservableMedia ) {
    super();
  }

  ngOnInit() {
    this.updateGrid();
    this.media.subscribe(() => {
        this.updateGrid();
    });
  }

  private updateGrid(): void {
    const isLarge = (this.media.isActive('xl') || this.media.isActive('lg') || this.media.isActive('md'));
    const isSmall = (this.media.isActive('sm'));
    this.gridColumns = isLarge ? 3 : (isSmall ? 2 : 1);
    this.gutterSize = isLarge ? 20 : 10;
  }

  buildScreen() {
    this.menuItems = this.screen.menuItems;
  }

  onEnter(value: string) {
    this.session.onAction('Save');
  }

  getClass(): String {
    // return 'main-menu-grid-list';
    return 'foo';
  }

  onMenuItemClick(menuItem: IActionItem) {
      this.session.onAction( menuItem );
  }

  public keybindsEnabled() {
    return Configuration.enableKeybinds;
  }

}
