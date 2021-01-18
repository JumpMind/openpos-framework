import { Component, ElementRef, HostListener, Renderer2 } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ConfigChangedMessage } from '../../messages/config-changed-message';
import { Status } from '../../messages/status.enum';
import { StatusMessage } from '../status.message';
import { StatusService } from '../status.service';
import { PeripheralSelectionService } from '../../peripherals/peripheral-selection.service';

@Component({
  selector: 'app-status-bar',
  templateUrl: './status-bar.component.html',
  styleUrls: ['./status-bar.component.scss']
})
export class StatusBarComponent {

  Status = Status;

  statusList$: Observable<StatusMessage[]>;
  systemInfo$: Observable<SystemInfo>;

  line1$: Observable<string>;

  constructor(
    private statusService: StatusService,
    peripheralSelectionService: PeripheralSelectionService,
    render2: Renderer2,
    elementRef: ElementRef
  ) {
    this.systemInfo$ = statusService.getSystemInfo().pipe(
      map(message => message as SystemInfo),
      tap(() => render2.addClass(elementRef.nativeElement, 'show'))
    );
    this.statusList$ = statusService.getStatus().pipe(
      map(statusMap => Array.from(statusMap.values()))
    );

    this.line1$ = combineLatest(
      this.systemInfo$,
      peripheralSelectionService.peripheralCategories$
    ).pipe(
      map(results => {
        let l = results[0].line1;
        let i = results[1];

        i.forEach((value) => {
          let dn = "Not Selected";

          if (value.selectedDevice && value.selectedDevice.displayName) {
            dn = value.selectedDevice.displayName;
          }

          l += " | " + value.name + " " + dn;
        });

        return l;
      })
    );
  }

  @HostListener('click')
  onClick() {
    this.statusService.openDetails();
  }
}

class SystemInfo extends ConfigChangedMessage {
  public line1: string;
  public line2: string;
}
