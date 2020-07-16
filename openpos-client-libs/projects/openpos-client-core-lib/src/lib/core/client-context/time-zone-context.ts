import {IClientContext} from './client-context-provider.interface';
import {Injectable} from '@angular/core';

@Injectable()
export class TimeZoneContext implements IClientContext {
    getContextProperties(): Map<string, string> {
        return new Map([
            ['timezoneOffset', this.getOffset()]
        ]);
    }

    /**
     * Gets the timezone offset in the ISO-8601 format.
     *
     * Examples:
     *
     *   +5:30   when timezone is ahead of UTC
     *   -01:00  when timezone is behind of UTC
     *   Z       when timezone is UTC
     */
    getOffset(): string {
        const timezoneOffsetMinutes = new Date().getTimezoneOffset();

        // If offset is 0, it means timezone is UTC
        if (timezoneOffsetMinutes === 0) {
            return 'Z';
        }

        const absTimezoneOffsetHours = Math.abs(timezoneOffsetMinutes / 60);
        const absTimezoneOffsetMinutes = Math.abs(timezoneOffsetMinutes % 60);
        let offsetHours = absTimezoneOffsetHours.toString(10);
        let offsetMinutes = absTimezoneOffsetMinutes.toString(10);

        if (absTimezoneOffsetHours < 10) {
            offsetHours = `0${offsetHours}`;
        }

        if (absTimezoneOffsetMinutes < 10) {
            offsetMinutes = `0${offsetMinutes}`;
        }

        // Add an opposite sign to the offset
        const direction = timezoneOffsetMinutes < 0 ? '+' : '-';
        return `${direction}${offsetHours}:${offsetMinutes}`;
    }
}
