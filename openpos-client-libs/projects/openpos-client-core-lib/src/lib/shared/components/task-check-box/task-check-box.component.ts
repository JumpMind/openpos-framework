import {ChangeDetectorRef, Component, EventEmitter, Input, OnDestroy, Optional, Output} from '@angular/core';
import {TaskListManagerService} from '../task-list/task-list-manager.service';

@Component({
  selector: 'app-task-check-box',
  templateUrl: './task-check-box.component.html',
  styleUrls: ['./task-check-box.component.scss']
})
export class TaskCheckBoxComponent implements OnDestroy{

  uncheckedIconName = 'check_box_outline_blank';
  checkedIconName = 'check_box';

  constructor( @Optional() private taskListManager: TaskListManagerService, private cd: ChangeDetectorRef ) {
    if(this.taskListManager){
      this.taskListManager.registerTaskCheckBox(this);
    }
  }

  private _checked: boolean;

  @Input()
  set checked( value: boolean) {
    if( value !== this._checked ) {
      this._checked = value;
      this.checkedChange.emit(this.checked);
      this.cd.detectChanges();
    }
  };

  get checked(): boolean {
      return this._checked;
  }

  @Output()
  checkedChange = new EventEmitter<boolean>();

  public onClick(){
    this.checked = !this.checked;
    this.checkedChange.emit(this.checked);
  }

  ngOnDestroy(): void {
    if( this.taskListManager ){
      this.taskListManager.removeTaskCheckBox(this);
    }
  }
}
