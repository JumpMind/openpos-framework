import { Pipe, PipeTransform } from '@angular/core';
import { FormattersService } from '../../core/services/formatters.service';

@Pipe({ name: 'localizeddatenotime' })
export class LocalizedDateNoTimePipe implements PipeTransform {

    constructor(private formatters: FormattersService) {
    }

    transform(value: any) {
        return this.formatters.getFormatter('monthdateyear').formatValue(value);
    }

}
