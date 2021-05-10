import {Injectable} from "@angular/core";
import {CapacitorService} from "./capacitor.service";
import {from, Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class WrapperService {
    constructor(private capacitorService: CapacitorService) {
    }

    public shouldAutoPersonalize() {
        return this.capacitorService.isRunningInCapacitor();
    }

    public getDeviceName(): Observable<string> {
        if (this.capacitorService.isRunningInCapacitor()) {
            return this.capacitorService.getDeviceName();
        }
        return from(null);
    }

}