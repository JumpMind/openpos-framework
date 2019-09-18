import {
    AfterContentInit,
    AfterViewInit,
    Component,
    ContentChild,
    ContentChildren, Directive,
    OnDestroy,
    QueryList
} from '@angular/core';
import {Subscription} from 'rxjs';
import {TaskCheckAllBoxComponent} from '../task-check-all-box/task-check-all-box.component';
import {TaskCheckAllStateEnum} from '../task-check-all-box/task-check-all-state.enum';
import {TaskCheckBoxComponent} from '../task-check-box/task-check-box.component';
import {TaskListManagerService} from './task-list-manager.service';

@Directive({
  selector: '[app-task-list]',
  providers: [ TaskListManagerService ]
})
export class TaskListComponent {

}
