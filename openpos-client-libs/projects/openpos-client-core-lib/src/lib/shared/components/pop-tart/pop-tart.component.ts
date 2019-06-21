import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-pop-tart',
  templateUrl: './pop-tart.component.html',
  styleUrls: ['./pop-tart.component.scss']
})
export class PopTartComponent {

  items: Array<string>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<PopTartComponent>) {
    this.items = data.optionItems;

  }

  public select(item: string) {
    this.dialogRef.close(item);
  }

}
