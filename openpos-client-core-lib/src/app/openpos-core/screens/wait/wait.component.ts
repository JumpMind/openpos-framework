import { Component, OnInit } from '@angular/core';

import { SessionService } from '../../services/session.service';

@Component({
  selector: 'app-wait-component',
  templateUrl: './wait.component.html'
})
export class WaitComponent implements OnInit {

  instructions = '';
  icon = '';
  screen: any;

  constructor(public session: SessionService) { }

  show(screen: any) {
    this.screen = screen;
  }

  ngOnInit() {
    this.instructions = this.screen.instructions;
    this.icon = this.screen.icon;
  }
}