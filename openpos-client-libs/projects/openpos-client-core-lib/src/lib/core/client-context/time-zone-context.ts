import { IClientContext } from './client-context-provider.interface';
import { Injectable } from '@angular/core';

@Injectable()
export class TimeZoneContext implements IClientContext {
    getContextProperties(): Map<string, string> {
        return new Map([
            ['timezoneOffset', (new Date().getTimezoneOffset() / 60).toString()]
        ]);
    }

}
