import { Component, OnInit } from '@angular/core';

import { SessionService } from '../../services/session.service';
import { IScreen } from '../..';

@Component({
  selector: 'app-call-for-authorization-component',
  templateUrl: './call-for-authorization.component.html'
})
export class CallForAuthorizationComponent implements OnInit, IScreen {

  prompt = '';
  instructions = '';

  constructor() { }

  show(screen: any) {

    this.prompt = screen.prompt;
    this.instructions = screen.instructions;
  }

  ngOnInit() {
  }
}