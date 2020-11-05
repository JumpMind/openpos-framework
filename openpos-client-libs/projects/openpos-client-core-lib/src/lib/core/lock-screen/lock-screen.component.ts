import {Component, Inject, Injector, OnDestroy, Optional} from '@angular/core';
import {Observable, Subject, Subscription} from 'rxjs';
import {takeUntil, tap} from 'rxjs/operators';
import { KeyPressProvider } from '../../shared/providers/keypress.provider';
import {ActionService} from '../actions/action.service';
import {LockScreenMessage} from '../messages/lock-screen-message';
import {LOCK_SCREEN_DATA} from './lock-screen.service';

@Component({
  selector: 'app-lock-screen',
  templateUrl: './lock-screen.component.html',
  styleUrls: ['./lock-screen.component.scss'],
  providers: [ActionService]
})
export class LockScreenComponent implements OnDestroy {

  data: LockScreenMessage;
  password = '';
  username = '';
  override = false;
  destroy = new Subject();

  constructor(
      @Inject(LOCK_SCREEN_DATA) data: Observable<LockScreenMessage>,
      private actionService: ActionService) {
      data.pipe(
          tap( message => this.data = message),
          takeUntil(this.destroy)
      ).subscribe();
  }

  submit($event: any) {
    $event.stopPropagation();
    if (this.override) {
      this.doOverride();
    } else {
      this.submitPassword();
    }
  }

  submitPassword() {
    this.actionService.doAction(this.data.passwordAction, this.password);
  }

  doOverride() {
    this.actionService.doAction(this.data.overrideAction, {username: this.username, password: this.password});
  }

  toggleOverride() {
    this.override = !this.override;
  }

  ngOnDestroy(): void {
    this.destroy.next();
  }

}
