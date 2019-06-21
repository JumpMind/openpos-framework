import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-searchable-pop-tart',
  templateUrl: './searchable-pop-tart.component.html',
  styleUrls: ['./searchable-pop-tart.component.scss']
})
export class SearchablePopTartComponent {

  items: Array<string>;
  filterValue: string;
  instructions: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<SearchablePopTartComponent>) {
    this.items = data.optionItems;
    this.instructions = data.instructions;
  }

  public select(item: string) {
    this.dialogRef.close(item);
  }

}
