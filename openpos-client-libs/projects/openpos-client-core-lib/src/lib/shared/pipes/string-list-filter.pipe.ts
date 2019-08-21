import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'stringListFilter'
})
export class StringListFilterPipe implements PipeTransform {

    constructor() { }

    // Filters a list to return any values to contain the filterValue
    transform(values: Array<string>, filterValue: string): Array<string> {
        if (!values || values.length === 0) {
            return null;
        }

        if (filterValue == null || filterValue === undefined || filterValue.trim() === '') {
            return values;
        }

        return values.filter(v => v.toLowerCase().search(filterValue.toLowerCase()) >= 0);
    }
}
