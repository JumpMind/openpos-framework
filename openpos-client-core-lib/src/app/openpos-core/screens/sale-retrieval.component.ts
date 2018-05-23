import { ISellItem } from '../common/isellitem';
import { IScreen } from '../common/iscreen';
import { IMenuItem } from '../common/imenuitem';
import {Component, ViewChild, AfterViewInit, DoCheck, OnInit} from '@angular/core';
import {SessionService} from '../services/session.service';

@Component({
  selector: 'app-sale-retrieval',
  templateUrl: './sale-retrieval.component.html'
})
export class SaleRetrievalComponent implements AfterViewInit, DoCheck, IScreen, OnInit {

  screen: any;
  constructor(public session: SessionService) {

  }
  public ngOnInit(): void {
  }

  show(screen: any) {
    this.screen = screen;
  }

  ngDoCheck(): void {
  }

  ngAfterViewInit(): void {
  }

  selected(value: string) {
    this.session.onAction('Next', value);
  }
}
