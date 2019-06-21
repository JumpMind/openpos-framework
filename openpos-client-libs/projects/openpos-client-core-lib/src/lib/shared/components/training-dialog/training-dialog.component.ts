import { Component, OnInit, Inject } from '@angular/core';
import { INSTRUCTIONS_DATA } from './training-token';

@Component({
  selector: 'app-training-dialog',
  templateUrl: './training-dialog.component.html',
  styleUrls: ['./training-dialog.component.scss']
})
export class TrainingDialogComponent implements OnInit {

  public instructions;

  constructor(@Inject(INSTRUCTIONS_DATA) public instructionsData) { }

  ngOnInit() {
    this.instructions = this.instructionsData;
  }

}
