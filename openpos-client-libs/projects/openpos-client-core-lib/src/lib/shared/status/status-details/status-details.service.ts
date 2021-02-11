import { Injectable } from "@angular/core";
import { PeripheralSelectionService } from "../../../core/peripherals/peripheral-selection.service";

@Injectable({
  providedIn: 'root'
})
export class StatusDetailsService {
  constructor(
    public peripheralSelection: PeripheralSelectionService
  ) {}

  isDetailsEmpty() {
    let count = 0;
    this.peripheralSelection.peripheralCategories$.subscribe(value => count += value.length);
    return count == 0;
  }
}
