import { Component, OnInit } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

/**
 * @ignore
 */
@Component({
  selector: 'app-call-for-authorization-component',
  templateUrl: './call-for-authorization.component.html'
})
export class CallForAuthorizationComponent extends PosScreen<any> implements OnInit {

  prompt = '';
  instructions = '';

  constructor() {
      super();
   }

  buildScreen() {
    this.prompt = this.screen.prompt;
    this.instructions = this.screen.instructions;
  }

  ngOnInit() {
  }
}
