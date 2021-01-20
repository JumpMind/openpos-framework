import { Component, ElementRef, HostListener, Renderer2 } from '@angular/core';
import { combineLatest, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { ConfigChangedMessage } from '../../../core/messages/config-changed-message';
import { Status } from '../../../core/messages/status.enum';
import { StatusMessage } from '../status.message';
import { StatusService } from '../status.service';
import { PeripheralCategory, PeripheralSelectionService } from '../../../core/peripherals/peripheral-selection.service';
import { LocaleService } from '../../../core/services/locale.service';

type CategoryWithName = PeripheralCategory & { displayName: string };

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
    elementRef: ElementRef,
    locale: LocaleService
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
      peripheralSelectionService.peripheralCategories$.pipe(
        switchMap(categories => combineLatest(
          categories.map(
            cat => {
              const split = cat.localizationKey.split(':');

              if (split.length !=2) {
                return of({ displayName: cat.localizationKey, ...cat});
              }

              return locale.getString(split[0], split[1]).pipe(
                map(r => (<CategoryWithName>{
                  displayName: r,
                  ...cat
                }))
              )
            }
          )
        ))
      )
    ).pipe(
      map(results => {
        let l = results[0].line1;
        let i = results[1];

        i.forEach((value) => {
          let dn = "Not Selected";

          if (value.selectedDevice && value.selectedDevice.displayName) {
            dn = value.selectedDevice.displayName;
          }

          l += " | " + value.displayName + " " + dn;
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
