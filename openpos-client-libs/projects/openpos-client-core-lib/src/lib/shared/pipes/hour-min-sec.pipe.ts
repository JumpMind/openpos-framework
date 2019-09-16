import { Pipe, PipeTransform } from '@angular/core';
import { formatNumber } from '@angular/common';
import {getHourMinSeconds} from '../../utilites/time-utils';

@Pipe({ name: 'hourminsec' })
export class HourMinSecPipe implements PipeTransform {

    constructor() {}

    transform(timeSeconds: number) {
        const time = getHourMinSeconds(timeSeconds);

        return `${formatNumber(time.hours, 'en', '2.0-0')}:${formatNumber(time.minutes, 'en', '2.0-0')}:${formatNumber(time.seconds, 'en', '2.0-0')}`;
  }
}
