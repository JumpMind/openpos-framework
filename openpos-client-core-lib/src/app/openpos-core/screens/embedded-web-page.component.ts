import { Router } from '@angular/router';
import { IScreen } from '../common/iscreen';
import { IMenuItem } from '../common/imenuitem';
import { Component, ViewChild, AfterViewInit, DoCheck } from '@angular/core';
import { SessionService } from '../services/session.service';

@Component({
  selector: 'app-embedded-web-page',
  templateUrl: './embedded-web-page.component.html'
})
export class EmbeddedWebPageComponent implements IScreen {

  screen: any;

  constructor(private session: SessionService,
    private router: Router) {
  }

  show(screen: any) {
    this.screen = screen;
  }

}
