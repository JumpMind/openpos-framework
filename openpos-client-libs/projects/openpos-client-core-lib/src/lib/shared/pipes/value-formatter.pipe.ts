import { Pipe, PipeTransform } from '@angular/core';
import { FormattersService } from '../../core/services/formatters.service';

@Pipe({ name: 'valueFormatter' })
export class ValueFormatterPipe implements PipeTransform {

    constructor( private formatters: FormattersService ) {}

    transform(value: string, formatterName: string): string {
        return this.formatters.getFormatter(formatterName).formatValue(value);
    }
}
