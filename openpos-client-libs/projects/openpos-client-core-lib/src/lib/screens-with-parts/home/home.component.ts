import { Component } from '@angular/core';
import { HomeInterface } from './home.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { Configuration } from '../../configuration/configuration';
import { Observable, from, timer } from 'rxjs';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { INotificationItem } from '../../core/interfaces/notification-item.interface';
import { trigger, state, style, transition, animate, keyframes, useAnimation } from '@angular/animations';
import { repeat, delay, tap, map } from 'rxjs/operators';
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
        useAnimation( bounceAnimation, {
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

    bounceInterval = timer(5000, 5000).pipe( map( i => i % 2 ? 'down' : 'move'));
  gutterSize: Observable<number>;
  gridColumns: Observable<number>;
  isMobile: Observable<boolean>;

  constructor( media: OpenposMediaService ) {
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
  }

  buildScreen() {}

  public keybindsEnabled() {
    return Configuration.enableKeybinds;
  }

  public getNotificationForButton(item: IActionItem): INotificationItem {
      if (this.screen.notificationItems) {
        return this.screen.notificationItems.find(i => i.id === item.action);
      }
      return null;
  }
}
