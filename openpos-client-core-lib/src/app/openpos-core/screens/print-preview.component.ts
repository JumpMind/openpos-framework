import { Component } from '@angular/core';
import { SessionService } from '../services/session.service';
import { IScreen } from '..';


@Component({
  selector: 'app-print-preview',
  templateUrl: './print-preview.component.html'
})
export class PrintPreviewComponent implements IScreen {

  screen: any;

  constructor(public session: SessionService) {
  }

  show(screen: any) {
    this.screen = screen;
  }


}
