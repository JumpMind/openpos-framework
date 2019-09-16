import {ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {TaskListManagerService} from '../task-list/task-list-manager.service';
import {TaskCheckAllStateEnum} from './task-check-all-state.enum';

@Component({
  selector: 'app-task-check-all-box',
  templateUrl: './task-check-all-box.component.html',
  styleUrls: ['./task-check-all-box.component.scss']
})
export class TaskCheckAllBoxComponent {

  uncheckedIconName = 'check_box_outline_blank';
  checkedIconName = 'check_box';
  someCheckedIconName = 'indeterminate_check_box';
  actionsIcon = 'arrow_drop_down';

  TaskCheckAllStateEnum = TaskCheckAllStateEnum;

  private _state = TaskCheckAllStateEnum.NoneChecked;

  constructor( taskListManager: TaskListManagerService, private cd: ChangeDetectorRef ) {
    taskListManager.registerCheckAllBox(this);
  }

  @Input()
  allActionName: string = 'All';

  @Input()
  noneActionName: string = 'None';

  @Input()
  set state( value: TaskCheckAllStateEnum) {
    this._state = value;
    this.cd.detectChanges();
  }

  get state(): TaskCheckAllStateEnum {
    return this._state;
  }
  @Output()
  stateChanged = new EventEmitter<TaskCheckAllStateEnum>()

  onClick( newState?: TaskCheckAllStateEnum) {
    if( !!newState ) {
      this.state = newState;
    } else {
      switch (this.state) {
        case TaskCheckAllStateEnum.AllChecked:
          this.state = TaskCheckAllStateEnum.NoneChecked;
          break;
        case TaskCheckAllStateEnum.NoneChecked:
          this.state = TaskCheckAllStateEnum.AllChecked;
          break;
        case TaskCheckAllStateEnum.SomeChecked:
          this.state = TaskCheckAllStateEnum.NoneChecked;
          break;
      }
    }
    this.stateChanged.emit(this.state);
  }
}
