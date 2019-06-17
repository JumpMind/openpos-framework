import { Directive, HostListener } from '@angular/core';
import { MatSelectionList, MatListOption } from '@angular/material';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: 'mat-selection-list[exclusiveSelect]'
})
export class MatExclusiveSelectionListDirective {

    @HostListener('selectionChange', ['$event.option', '$event.source'])
    onSelectionChange(option: MatListOption, source: MatSelectionList) {
        if (option.selected) {
            source.options.forEach(o => {
                    if (o !== option) {
                        o.selected = false;
                    }
                });
        }
    }

}
