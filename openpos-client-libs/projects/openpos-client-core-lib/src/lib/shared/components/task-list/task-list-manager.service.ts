import {Injectable, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs';
import {TaskCheckAllBoxComponent} from '../task-check-all-box/task-check-all-box.component';
import {TaskCheckAllStateEnum} from '../task-check-all-box/task-check-all-state.enum';
import {TaskCheckBoxComponent} from '../task-check-box/task-check-box.component';

@Injectable()
export class TaskListManagerService implements OnDestroy {

    private checkBoxes = new Set();
    private checkAllBox: TaskCheckAllBoxComponent;

    private subscriptions = new Map<TaskCheckBoxComponent, Subscription>();

    registerTaskCheckBox( taskCheckBox: TaskCheckBoxComponent ) {
        this.checkBoxes.add(taskCheckBox);
        this.subscriptions.set( taskCheckBox, taskCheckBox.checkedChange.subscribe(value => this.checkboxChanged()));
    }

    removeTaskCheckBox( taskCheckBox: TaskCheckBoxComponent ) {
        this.checkBoxes.delete(taskCheckBox);
        this.subscriptions.get(taskCheckBox).unsubscribe();
        this.subscriptions.delete(taskCheckBox);
    }

    registerCheckAllBox( checkAllBox: TaskCheckAllBoxComponent ) {
        this.checkAllBox = checkAllBox;
        this.checkAllBox.stateChanged.subscribe( value => this.checkAllChanged(value));
    }

    private checkboxChanged() {
        let checkedCheckBoxes = [...this.checkBoxes].filter( item => item.checked);
        if( checkedCheckBoxes.length === 0 ){
            this.checkAllBox.state = TaskCheckAllStateEnum.NoneChecked;
        } else if( checkedCheckBoxes.length === this.checkBoxes.size ){
            this.checkAllBox.state = TaskCheckAllStateEnum.AllChecked;
        } else {
            this.checkAllBox.state = TaskCheckAllStateEnum.SomeChecked;
        }
    }

    private checkAllChanged( state: TaskCheckAllStateEnum) {
        switch(state){
          case TaskCheckAllStateEnum.NoneChecked:
            this.checkBoxes.forEach( item => item.checked = false );
            break;
          case TaskCheckAllStateEnum.AllChecked:
            this.checkBoxes.forEach( item => item.checked = true);
            break;
        }
    }

    ngOnDestroy(): void {
        [...this.subscriptions.values()].forEach( value => value.unsubscribe());
    }
}