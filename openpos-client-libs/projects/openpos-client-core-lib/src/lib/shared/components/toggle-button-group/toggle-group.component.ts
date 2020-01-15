import { Component, ContentChildren, QueryList, AfterViewInit, Input, Output, EventEmitter, DoCheck, OnDestroy, OnChanges, SimpleChange, SimpleChanges } from '@angular/core';
import { ToggleButtonComponent } from '../toggle-button/toggle-button.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toggle-group',
  templateUrl: './toggle-group.component.html',
  styleUrls: ['./toggle-group.component.scss']
})
export class ToggleGroupComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input()
  value: any;

  @Output() 
  valueChange = new EventEmitter();
  
  private buttonChangeSubscriptions = new Subscription();
  private subscriptions = new Subscription();

  @ContentChildren(ToggleButtonComponent, {descendants: true}) toggleButtons: QueryList<ToggleButtonComponent>;

  constructor() {
  }

  private subscribeForChildChanges() {
    this.toggleButtons.forEach( button => {
        this.buttonChangeSubscriptions.add( button.change.subscribe(event => this.onToggleChange(event.source, event.value)));
      });
  }

  ngAfterViewInit(): void {
    this.subscribeForChildChanges();
    // Handle cases when children are updated dynamically
    this.subscriptions.add(this.toggleButtons.changes.subscribe( x => {
        this.buttonChangeSubscriptions.unsubscribe();
        this.subscribeForChildChanges();
    }));
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.toggleButtons) { return; }
    this.toggleButtons.forEach( button => {
      if (button.value === this.value && !button.selected) {
          button.selected = true;
      } else if (button.value !== this.value && !!button.selected) {
          button.selected = false;
      }
    });
  }

  onToggleChange( source: ToggleButtonComponent, value: any) {
    if ( source.selected ) {
      this.toggleButtons.filter(button => button.value !== source.value && !!button.selected).forEach(button => button.selected = false);
      this.valueChange.emit(value);
    } else if (this.toggleButtons.filter(button => button.selected === true).length < 1) {
      this.valueChange.emit(null);
    }

  }

  ngOnDestroy(): void {
      this.subscriptions.unsubscribe();
      this.buttonChangeSubscriptions.unsubscribe();
  }
}
