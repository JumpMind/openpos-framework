import { Component } from '@angular/core';
import { SaleInterface } from './sale.interface';
import { Observable } from 'rxjs/internal/Observable';
import { MatDialog } from '@angular/material';
import { PosScreen } from '../pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { ITotal } from '../../core/interfaces/total.interface';

@ScreenComponent({
    name: 'Sale'
})
@Component({
  selector: 'app-sale',
  templateUrl: './sale.component.html',
  styleUrls: ['./sale.component.scss']
})
export class SaleComponent extends PosScreen<SaleInterface> {

  overFlowListSize: Observable<number>;
  trainingDrawerOpen = false;
  totals: ITotal[];

  constructor(protected mediaService: OpenposMediaService, protected dialog: MatDialog) {
    super();
    this.overFlowListSize = this.mediaService.mediaObservableFromMap(new Map([
        ['xs', 3],
        ['sm', 3],
        ['md', 4],
        ['lg', 5],
        ['xl', 5]
      ]));

  }

  buildScreen() {
    // Reallocate totals array to force change detection in child app-overflow-list
    this.totals = this.screen.totals ? this.screen.totals.slice() : [];
    this.screen.customerName = this.screen.customerName != null && this.screen.customerName.length > 10 ?
      this.screen.customerName.substring(0, 10) + '...' : this.screen.customerName;
    this.dialog.closeAll();
  }

  onEnter(value: string) {
    this.session.onAction('Next', value);
  }
}
