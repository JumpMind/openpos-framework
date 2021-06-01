import { InjectionToken } from "@angular/core";
import { Observable } from "rxjs";

export const ZEROCONF_TOKEN = new InjectionToken<Array<Zeroconf>>("ZEROCONF");

export interface Zeroconf {
    watch(type: string, domain: string): Observable<ZeroconfResult>;

    deviceName(): Observable<string>;

    isAvailable(): Observable<boolean>;
}

export interface ZeroconfService {
    domain: string;
    type: string;
    name: string;
    port: number;
    hostname: string;
    ipv4Addresses: string[];
    ipv6Addresses: string[];
    txtRecord: any;
}

export interface ZeroconfResult {
    action: 'registered' | 'added' | 'removed' | 'resolved';
    service: ZeroconfService;
}
