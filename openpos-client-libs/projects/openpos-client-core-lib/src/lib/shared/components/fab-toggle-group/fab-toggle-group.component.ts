import { Component, ContentChildren, QueryList, AfterViewInit, Input, Output, EventEmitter, DoCheck } from '@angular/core';
import { FabToggleButtonComponent } from '../fab-toggle-button/fab-toggle-button.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-fab-toggle-group',
  templateUrl: './fab-toggle-group.component.html',
  styleUrls: ['./fab-toggle-group.component.scss']
})
export class FabToggleGroupComponent implements AfterViewInit, DoCheck {


  @Input() value;
  @Output() valueChange = new EventEmitter();
  private buttonChangeSubscriptions: Subscription[] = [];

  @ContentChildren(FabToggleButtonComponent, {descendants: true}) toggleButtons: QueryList<FabToggleButtonComponent>;

  constructor() {

  }

  private subscribeForChildChanges() {
    this.buttonChangeSubscriptions = [];
    this.toggleButtons.forEach( button => {
        const subscr = button.change.subscribe(event => this.onToggleChange(event.source, event.value));
        this.buttonChangeSubscriptions.push(subscr);
      });
  }

  ngAfterViewInit(): void {
    this.subscribeForChildChanges();
    // Handle cases when children are updated dynamically
    this.toggleButtons.changes.subscribe( x => {
        this.buttonChangeSubscriptions.forEach(s => s.unsubscribe());
        this.subscribeForChildChanges();
    });
  }

  ngDoCheck(): void {
    if (!this.toggleButtons) { return; }
    this.toggleButtons.forEach( button => {
      if ( button.value === this.value ) {
        button.selected = true;
      } else {
        button.selected = false;
      }
    });
  }

  onToggleChange( source: FabToggleButtonComponent, value: any) {
    if ( source.selected ) {
      this.valueChange.emit(value);
      this.toggleButtons.filter(button => button.value !== source.value).forEach(button => button.setSelected(false));
    } else if (this.toggleButtons.filter(button => button.selected === true).length < 1) {
      this.valueChange.emit(null);
    }

  }
}
