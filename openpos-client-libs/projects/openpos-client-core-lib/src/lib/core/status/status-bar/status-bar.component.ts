import {Component, OnDestroy} from '@angular/core';
import {Subject} from 'rxjs';
import {map, takeUntil, tap} from 'rxjs/operators';
import {Status} from '../../messages/status.enum';
import {StatusMessage} from '../status.message';
import {StatusService} from '../status.service';

@Component({
  selector: 'app-status-bar',
  templateUrl: './status-bar.component.html',
  styleUrls: ['./status-bar.component.scss']
})
export class StatusBarComponent implements OnDestroy {

  private destroy$ = new Subject();

  systemInfo: Map<string, string>;
  statusList: StatusMessage[];

  Status = Status;

  constructor( statusService: StatusService) {
    statusService.getSystemInfo().pipe(
        tap( info => this.systemInfo = info),
        takeUntil(this.destroy$)
    ).subscribe();

    statusService.getStatus().pipe(
        map( statusMap => statusMap.values()),
        tap( statusList => this.statusList = Array.from(statusList)),
        takeUntil(this.destroy$)
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
  }

}
