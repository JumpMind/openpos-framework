import {Injectable} from "@angular/core";
import {CapacitorService} from "./capacitor.service";

@Injectable({
    providedIn: 'root'
})
export class WrapperService {
    constructor(private capacitorService: CapacitorService) {
    }

    public shouldAutoPersonalize() {
        return this.capacitorService.isRunningInCapacitor();
    }

}