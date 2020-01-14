import { Directive } from '@angular/core';
import {TaskListManagerService} from './task-list-manager.service';

@Directive({
  selector: '[app-task-list]',
  providers: [ TaskListManagerService ]
})
export class TaskListComponent {

}
