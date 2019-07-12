import {Component} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import { IPromoItem } from './promo-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ISellItem } from '../../core/interfaces/sell-item.interface';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'SellItemDetail'
})
@Component({
  selector: 'app-sell-item-detail',
  templateUrl: './sell-item-detail.component.html',
  styleUrls: ['./sell-item-detail.component.scss']
})

export class SellItemDetailComponent extends PosScreen<any> {
  public item: ISellItem;

  promosDataSource: MatTableDataSource<IPromoItem>;

  constructor() {
      super();
  }

  buildScreen() {
    this.item = this.screen.item;
    this.promosDataSource = new MatTableDataSource(this.screen.promos);
  }

  onIdClick(id: any) {
    this.session.onAction('LinkClicked', id);
  }

}
