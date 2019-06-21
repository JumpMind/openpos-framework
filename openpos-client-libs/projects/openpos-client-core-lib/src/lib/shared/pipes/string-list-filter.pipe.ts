import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'stringListFilter'})
export class StringListFilterPipe implements PipeTransform {

    constructor() {}

    // Filters a list to return any values to contain the filterValue
    transform(values: Array<string>, filterValue: string): Array<string> {
        if ( filterValue == null || filterValue === undefined || !values ) {
            return null;
        }

        if ( filterValue.trim() === '') {
            return [];
        }

        return values.filter( v => v.toLowerCase().search(filterValue.toLowerCase()) >= 0);
    }
}
