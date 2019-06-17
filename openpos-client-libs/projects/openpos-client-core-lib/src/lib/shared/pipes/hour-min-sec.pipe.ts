import { Pipe, PipeTransform } from '@angular/core';
import { formatNumber } from '@angular/common';

@Pipe({ name: 'hourminsec' })
export class HourMinSecPipe implements PipeTransform {

    constructor() {}

    transform(timeSeconds: number) {
        const hour = Math.floor(timeSeconds / (60 * 60));
        const min = Math.floor((timeSeconds - (hour * 3600)) / 60);
        const sec = timeSeconds - (hour * 3600) - (min * 60 );

        return `${formatNumber(hour, 'en', '2.0-0')}:${formatNumber(min, 'en', '2.0-0')}:${formatNumber(sec, 'en', '2.0-0')}`;
    }
}
