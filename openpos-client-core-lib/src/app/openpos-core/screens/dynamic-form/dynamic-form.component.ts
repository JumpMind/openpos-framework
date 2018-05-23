import { IScreen } from '../../common/iscreen';
import { Component } from '@angular/core';
import { SessionService } from '../../services/session.service';
import {NgForm} from '@angular/forms';


@Component({
  selector: 'app-dynamic-form',
  templateUrl: './dynamic-form.component.html'
})
export class DynamicFormComponent implements IScreen {

  screen: any;

  constructor(public session: SessionService) {
  }

  show(screen: any) {
    this.screen = screen;
  }

}
