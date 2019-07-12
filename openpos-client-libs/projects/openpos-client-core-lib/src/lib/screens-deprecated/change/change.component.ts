import { Component, AfterViewInit, DoCheck } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { ITenderItem } from '../../core/interfaces/tender-item.interface';

/**
 * @ignore
 */
@Component({
  selector: 'app-change',
  templateUrl: './change.component.html',
  styleUrls: ['./change.component.scss']
})
export class ChangeComponent extends PosScreen<any> implements AfterViewInit, DoCheck {

  total: string;
  tendered: string;
  balanceDue: string;
  screen: any;

  itemsDataSource: MatTableDataSource<ITenderItem>;

  buildScreen() { }

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
