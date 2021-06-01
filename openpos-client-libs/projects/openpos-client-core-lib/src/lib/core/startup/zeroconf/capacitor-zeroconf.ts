import { Injectable } from '@angular/core';
import * as CAP from '@ionic-native/zeroconf';
import { Observable, of } from "rxjs";
import { CapacitorService } from '../../services/capacitor.service';
import { Zeroconf, ZeroconfResult } from "./zeroconf";

@Injectable()
export class CapacitorZeroconf implements Zeroconf {
    constructor(private capacitorService: CapacitorService) {}
    
    isAvailable(): Observable<boolean> {
        return of(this.capacitorService.isRunningInCapacitor());
    }

    watch(type: string, domain: string): Observable<ZeroconfResult> {
        return CAP.Zeroconf.watch(type, domain);
    }

    deviceName(): Observable<string> {
        return this.capacitorService.getDeviceName();
    }
}
