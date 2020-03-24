import {Component, OnDestroy} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {map, takeUntil, tap} from 'rxjs/operators';
import {Status} from '../../messages/status.enum';
import {StatusMessage} from '../status.message';
import {StatusService} from '../status.service';

@Component({
  selector: 'app-status-bar',
  templateUrl: './status-bar.component.html',
  styleUrls: ['./status-bar.component.scss']
})
export class StatusBarComponent {

  Status = Status;

  statusList$: Observable<StatusMessage[]>;
  systemInfo$: Observable<SystemInfoEntry[]>

  constructor( public statusService: StatusService) {
    this.systemInfo$ = statusService.getSystemInfo().pipe(
        map( info => Array.from(info.entries()).map( entry => new SystemInfoEntry(entry[0], entry[1])))
    );
    this.statusList$ = statusService.getStatus().pipe(
        map( statusMap => Array.from(statusMap.values()))
    );
  }

}


class SystemInfoEntry {
  constructor(public label: string, public value: string) {
  }
}