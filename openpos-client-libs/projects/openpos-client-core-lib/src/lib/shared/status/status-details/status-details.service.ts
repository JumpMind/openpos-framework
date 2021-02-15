import { Injectable } from "@angular/core";
import { PeripheralSelectionService } from "../../../core/peripherals/peripheral-selection.service";
import { map, take } from "rxjs/operators";
import { Observable } from "rxjs/internal/Observable";

@Injectable({
  providedIn: 'root'
})
export class StatusDetailsService {
  constructor(
    public peripheralSelection: PeripheralSelectionService
  ) {}

  isDetailsNotEmpty(): Observable<boolean> {
    return this.peripheralSelection.peripheralCategories$.pipe(
      take(1),
      map(v => v.length > 0)
    );
  }
}
