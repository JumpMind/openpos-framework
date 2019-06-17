import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-kebab-menu',
  templateUrl: './kebab-menu.component.html',
  styleUrls: ['./kebab-menu.component.scss']
})
export class KebabMenuComponent {

    constructor( @Inject(MAT_DIALOG_DATA) public data: any ) {

    }
}
