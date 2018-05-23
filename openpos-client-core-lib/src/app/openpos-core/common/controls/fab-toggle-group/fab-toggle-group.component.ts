import { Component, ContentChildren, QueryList, AfterViewInit, Input, Output, EventEmitter, Directive, DoCheck } from '@angular/core';
import { FabToggleChange, FabToggleButtonComponent } from '../fab-toggle-button/fab-toggle-button.component';

@Component({
  selector: 'app-fab-toggle-group',
  templateUrl: './fab-toggle-group.component.html',
  styleUrls: ['./fab-toggle-group.component.scss']
})
export class FabToggleGroupComponent implements AfterViewInit, DoCheck {


  @Input() value;
  @Output() valueChange = new EventEmitter();

  @ContentChildren(FabToggleButtonComponent) toggleButtons : QueryList<FabToggleButtonComponent>;

  constructor() { 
      
  }

  ngAfterViewInit(): void {
    this.toggleButtons.forEach( button => {
      button.change.subscribe(event => this.onToggleChange(event.source, event.value));
    });
  }

  ngDoCheck(): void {
    if(!this.toggleButtons) return;
    this.toggleButtons.forEach( button => {
      if( button.value == this.value ){
        button.selected = true;
      }else{
        button.selected = false;
      }
    });
  }

  onToggleChange( source: FabToggleButtonComponent, value: any){
    if( source.selected ){
      this.valueChange.emit(value);
      this.toggleButtons.filter(button => button.value != source.value).forEach(button => button.setSelected(false));
    } else if (this.toggleButtons.filter(button => button.selected == true).length < 1) {
      this.valueChange.emit(null);
    }

  }
}
