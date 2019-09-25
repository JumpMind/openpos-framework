import { Component, ViewChild, ChangeDetectorRef } from '@angular/core';
import { HomeInterface } from './home.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { Configuration } from '../../configuration/configuration';
import { Observable, timer } from 'rxjs';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { INotificationItem } from '../../core/interfaces/notification-item.interface';
import { trigger, state, style, transition, useAnimation } from '@angular/animations';
import { map } from 'rxjs/operators';
import { bounceAnimation } from '../../shared/animations/bounce.animation';

@ScreenComponent({
  name: 'Home'
})
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  animations: [
    trigger('bounce', [
      // ...
      state('move', style({
        transform: 'translateY(0)'
      })),
      transition('* => move', [
        useAnimation(bounceAnimation, {
          params: {
            height: '100px',
            time: '2s'
          }
        })
      ])
    ]),
  ]
})
export class HomeComponent extends PosScreen<HomeInterface> {

  bounceInterval = timer(5000, 5000).pipe(map(i => i % 2 ? 'down' : 'move'));
  gutterSize: Observable<number>;
  gridColumns: Observable<number>;
  isMobile: Observable<boolean>;
  pageNum = 0;
  pageSize = 0;
  paginationSettings = { itemsPerPage: this.pageSize, currentPage: this.pageNum };
  
  constructor(media: OpenposMediaService) {
    super();
    this.gridColumns = media.mediaObservableFromMap(new Map([
      ['xs', 1],
      ['sm', 2],
      ['md', 3],
      ['lg', 3],
      ['xl', 3]
    ]));

    this.gutterSize = media.mediaObservableFromMap(new Map([
      ['xs', 10],
      ['sm', 10],
      ['md', 20],
      ['lg', 20],
      ['xl', 20]
    ]));

    this.isMobile = media.mediaObservableFromMap(new Map([
      ['xs', true],
      ['sm', false],
      ['md', false],
      ['lg', false],
      ['xl', false]
    ]));

    // Menu items per page
    media.mediaObservableFromMap(new Map([
      ['xs', 3],
      ['sm', this.pageSize/2],
      ['md', this.pageSize],
      ['lg', this.pageSize],
      ['xl', this.pageSize]
    ])).subscribe((val => {
      this.pageNum = 0;
      this.pageSize = val;
      this.updatePaginationSettings();
    }));
  }

  buildScreen() { }

  public keybindsEnabled() {
    return Configuration.enableKeybinds;
  }

  public getNotificationForButton(item: IActionItem): INotificationItem {
    if (this.screen.notificationItems) {
      return this.screen.notificationItems.find(i => i.id === item.action);
    }
    return null;
  }

  public changePage($event) {
    this.pageNum = $event;
    this.updatePaginationSettings();
  }

  public updatePaginationSettings() {
    this.paginationSettings = { itemsPerPage: this.pageSize, currentPage: this.pageNum };
  }
}
