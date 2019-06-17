import { Component, ContentChildren, QueryList, AfterViewInit, Input, Output, EventEmitter, DoCheck, OnDestroy } from '@angular/core';
import { ToggleButtonComponent } from '../toggle-button/toggle-button.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toggle-group',
  templateUrl: './toggle-group.component.html',
  styleUrls: ['./toggle-group.component.scss']
})
export class ToggleGroupComponent implements AfterViewInit, DoCheck, OnDestroy {
  @Input()
  value: any;

  @Output() 
  valueChange = new EventEmitter();
  
  private buttonChangeSubscriptions: Subscription[] = [];

  @ContentChildren(ToggleButtonComponent, {descendants: true}) toggleButtons: QueryList<ToggleButtonComponent>;

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
        button.setSelected(true);
      } else {
        button.setSelected(false);
      }
    });
  }

  onToggleChange( source: ToggleButtonComponent, value: any) {
    if ( source.selected ) {
      this.valueChange.emit(value);
      this.toggleButtons.filter(button => button.value !== source.value).forEach(button => button.setSelected(false));
    } else if (this.toggleButtons.filter(button => button.selected === true).length < 1) {
      this.valueChange.emit(null);
    }

  }

  ngOnDestroy(): void {
    if (this.buttonChangeSubscriptions) {
      this.buttonChangeSubscriptions.forEach( sub => sub.unsubscribe());
    }
  }
}
