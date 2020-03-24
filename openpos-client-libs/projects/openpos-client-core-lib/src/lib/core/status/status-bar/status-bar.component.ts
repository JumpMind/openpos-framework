import {Component, ElementRef, Renderer2} from '@angular/core';
import {Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
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
  systemInfo$: Observable<SystemInfoEntry[]>;

  constructor( statusService: StatusService, render2: Renderer2, elementRef: ElementRef) {
    this.systemInfo$ = statusService.getSystemInfo().pipe(
        map( info => Array.from(info.entries()).map( entry => new SystemInfoEntry(entry[0], entry[1]))),
        tap( info => render2.addClass(elementRef.nativeElement, 'show'))
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