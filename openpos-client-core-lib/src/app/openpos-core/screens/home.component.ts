import { IMenuItem } from './../common/imenuitem';
import { IconComponent } from './../common/controls/icon.component';
import { IScreen } from '../common/iscreen';
import { Component, ViewChild, OnInit } from '@angular/core';
import { SessionService } from '../services/session.service';
import {MediaChange, ObservableMedia} from '@angular/flex-layout';
import { IUrlMenuItem } from '../common/iurlmenuitem';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements IScreen, OnInit {

  screen: any;
  public menuItems: IMenuItem[];
  gutterSize = 40;
  gridColumns = 3;

  constructor(public session: SessionService, public media: ObservableMedia) {

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

  show(screen: any) {
    this.screen = screen;
    this.menuItems = screen.menuItems;
  }

  onEnter(value: string) {
    this.session.onAction('Save');
  }

  getClass(): String {
    // return 'main-menu-grid-list';
    return 'foo';
  }

  onMenuItemClick(menuItem: IMenuItem) {
      this.session.onAction(menuItem, null, menuItem.confirmationMessage );
  }
}
