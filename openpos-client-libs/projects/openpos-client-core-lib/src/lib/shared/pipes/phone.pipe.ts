import { Pipe, PipeTransform } from '@angular/core';
import { FormattersService } from '../../core/services/formatters.service';

@Pipe({ name: 'phone' })
export class PhonePipe implements PipeTransform {

    constructor( private formatters: FormattersService ) {}

    transform(phone: string) {
        return this.formatters.getFormatter('phone').formatValue(phone);
    }
}
