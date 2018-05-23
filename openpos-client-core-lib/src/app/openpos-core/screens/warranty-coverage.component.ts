import { ProductListComponent } from './../common/controls/product-list.component';
import { IItem } from './../common/iitem';
import { IScreen } from '../common/iscreen';
import { Component, ViewChild, AfterViewInit, DoCheck, OnInit } from '@angular/core';
import { SessionService } from '../services/session.service';
import { MatSelectionList, MatListOption } from '@angular/material';

@Component({
  selector: 'app-warranty-coverage',
  templateUrl: './warranty-coverage.component.html'
})
export class WarrantyCoverageComponent implements IScreen, OnInit {
  screen: any;
  text: string;
  warrantyItems: IItem[];
  warrantyCostTotal: string;
  @ViewChild(MatSelectionList) warrantyItemsSelectionList: MatSelectionList;


  constructor(public session: SessionService) {

  }

  show(screen: any) {
    this.screen = screen;
  }

  ngOnInit(): void {
    this.text = this.screen.text;
    this.warrantyItemsSelectionList.selectedOptions.clear();
    this.warrantyItems = this.screen.warrantyItems;
    this.warrantyCostTotal = this.screen.warrantyCostTotal;
  }

  onItemSelected(event: Event) {
    const selectedIndexes: number[] = [];

    // FWIW, I've been unable to get binding directly from the mat-list-option.selected property
    // to the warrantyItem.selected to work properly, so instead I get the select options
    // from the warrantyItemsSelectionList.  Not ideal, but it works.
    if (this.warrantyItemsSelectionList.selectedOptions.hasValue) {
      this.warrantyItemsSelectionList.selectedOptions.selected.forEach(
        (matListOption: MatListOption, index: number, array: MatListOption[]) => {
          selectedIndexes.push(matListOption.value);
        }
      );
    }

    this.session.response = selectedIndexes.sort();
    this.session.onAction('SelectedItems');
  }

  onMenuAction(action: string): void {
    this.session.onAction(action);
  }
}
