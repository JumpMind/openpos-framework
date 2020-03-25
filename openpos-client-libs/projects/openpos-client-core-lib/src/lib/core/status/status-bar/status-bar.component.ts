import {Component, ElementRef, Renderer2} from '@angular/core';
import {Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {ConfigChangedMessage} from '../../messages/config-changed-message';
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
  systemInfo$: Observable<SystemInfo>;

  constructor( statusService: StatusService, render2: Renderer2, elementRef: ElementRef) {
    this.systemInfo$ = statusService.getSystemInfo().pipe(
        map( message => message as SystemInfo),
        tap( info => render2.addClass(elementRef.nativeElement, 'show'))
    );
    this.statusList$ = statusService.getStatus().pipe(
        map( statusMap => Array.from(statusMap.values()))
    );
  }

}

class SystemInfo extends ConfigChangedMessage{
  public line1: string;
  public line2: string;
}