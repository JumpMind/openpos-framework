import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {takeUntil, tap} from 'rxjs/operators';
import {ActionService} from '../actions/action.service';
import {LockScreenMessage} from '../messages/lock-screen-message';
import {LOCK_SCREEN_DATA} from './lock-screen.service';

@Component({
  selector: 'app-lock-screen',
  templateUrl: './lock-screen.component.html',
  styleUrls: ['./lock-screen.component.scss']
})
export class LockScreenComponent implements OnDestroy{

  data: LockScreenMessage;
  password = "";

  destroy = new Subject();

  constructor(
      @Inject(LOCK_SCREEN_DATA) data: Observable<LockScreenMessage>,
      private actionService: ActionService) {
      data.pipe(
          tap( message => this.data = message),
          takeUntil(this.destroy)
      ).subscribe();
  }

  submitPassword(){
    this.actionService.doAction(this.data.passwordAction, this.password);
  }

  ngOnDestroy(): void {
    this.destroy.next();
  }

}
