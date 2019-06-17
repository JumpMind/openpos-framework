import { Component, OnInit } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

/**
 * @ignore
 */
@Component({
  selector: 'app-wait-component',
  templateUrl: './wait.component.html'
})
export class WaitComponent extends PosScreen<any> {

  instructions = '';
  icon = '';

  buildScreen() {
    this.instructions = this.screen.instructions;
    this.icon = this.screen.icon;
  }
}
