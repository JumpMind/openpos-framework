import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'listLimit'})
export class ListLimitPipe implements PipeTransform {

    constructor() {}

    transform(values: Array<string>, limit: number): Array<string> {
        if ( !values || limit == null || limit === undefined) {
            return null;
        }
        return values.slice(0, limit);
    }
}
