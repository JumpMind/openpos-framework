import { DeviceService } from '../../services/device.service';
import { ISellItem } from '../../common/isellitem';
import { IScreen } from '../../common/iscreen';
import { IMenuItem } from '../../common/imenuitem';
import { Component, ViewChild, AfterViewInit, DoCheck } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { ITenderItem } from '../../common/itenderitem';
import { MatTableDataSource } from '@angular/material';

@Component({
  selector: 'app-change',
  templateUrl: './change.component.html',
  styleUrls: ['./change.component.scss']
})
export class ChangeComponent implements AfterViewInit, DoCheck, IScreen {

  total: string;
  tendered: string;
  balanceDue: string;
  screen: any;

  itemsDataSource: MatTableDataSource<ITenderItem>;

  constructor(public session: SessionService, devices: DeviceService) {

  }

  show(screen: any) {
    this.screen = screen;
  }

  ngDoCheck(): void {
    if (typeof this.screen !== 'undefined') {
      this.itemsDataSource = new MatTableDataSource(this.screen.items);
    }
  }

  ngAfterViewInit(): void {
  }


  onEnter(value: string) {
  }


}
