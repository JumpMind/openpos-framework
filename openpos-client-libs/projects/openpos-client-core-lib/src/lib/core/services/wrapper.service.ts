import {Injectable} from "@angular/core";
import {CapacitorService} from "./capacitor.service";
import {CordovaService} from "./cordova.service"
import {from, Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class WrapperService {
    constructor(private capacitorService: CapacitorService, private cordovaService: CordovaService) {
    }

    public shouldAutoPersonalize() {
        if (this.cordovaService.isRunningInCordova()) {
            return this.cordovaService.isRunningInCordova();
        } else {
            return this.capacitorService.isRunningInCapacitor();
        }
    }

    public getDeviceName(): Observable<string> {
        if (this.capacitorService.isRunningInCapacitor()) {
            return this.capacitorService.getDeviceName();
        } else if (this.cordovaService.isRunningInCordova()) {
            return this.cordovaService.getDeviceName();
        }
        return from(null);
    }

}