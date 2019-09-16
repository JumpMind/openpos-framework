import {ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {TaskListManagerService} from '../task-list/task-list-manager.service';

@Component({
  selector: 'app-task-check-box',
  templateUrl: './task-check-box.component.html',
  styleUrls: ['./task-check-box.component.scss']
})
export class TaskCheckBoxComponent {

  uncheckedIconName = 'check_box_outline_blank';
  checkedIconName = 'check_box';

  constructor( taskListManager: TaskListManagerService, private cd: ChangeDetectorRef ) {
    taskListManager.registerTaskCheckBox(this);
  }

  private _checked: boolean;

  @Input()
  set checked( value: boolean) {
      this._checked = value;
      this.cd.detectChanges();
  };

  get checked(): boolean {
      return this._checked;
  }

  @Output()
  checkChanged = new EventEmitter<boolean>();

  public onClick(){
    this.checked = !this.checked;
    this.checkChanged.emit(this.checked);
  }
}
