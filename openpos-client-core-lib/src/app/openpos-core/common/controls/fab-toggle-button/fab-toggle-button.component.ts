import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatButtonToggleChange } from '@angular/material';

@Component({
  selector: 'app-fab-toggle-button',
  templateUrl: './fab-toggle-button.component.html',
  styleUrls: ['./fab-toggle-button.component.scss']
})
export class FabToggleButtonComponent{

    @Input()
    icon: string;

    @Input()
    value: string;

    @Input()
    selected: boolean;

    @Input()
    disabled: boolean;

    @Output()
    selectedChange = new EventEmitter();

    @Output()
    change: EventEmitter<FabToggleChange> = new EventEmitter<FabToggleChange>();

  constructor() { 
      
  }

  onClick(){
    this.setSelected(!this.selected);
    this.change.emit(new FabToggleChange(this, this.value));
    
  }

  setSelected(value: boolean){
    this.selected = value;
    this.selectedChange.emit(this.selected);
  }

}

export class FabToggleChange {
  constructor( public source: FabToggleButtonComponent, public value: any) {}
}
