import { Component, Input } from '@angular/core';
import { DisplayProperty } from './display-property.interface';
import { DisplayPropertyAligment } from './display-property-alignment.enum';

@Component({
    selector: 'app-display-property',
    templateUrl: './display-property.component.html',
    styleUrls: ['./display-property.component.scss'],
})
export class DisplayPropertyComponent {

    DisplayPropertyAlignment = DisplayPropertyAligment;

    @Input() alignmentType = DisplayPropertyAligment.column;
    @Input() set label( label: string) {
        this.property.label = label;
    }
    @Input() set value( value: string) {
        this.property.value = value;
    }
    @Input() set valueFormatter( formatter: string) {
        this.property.valueFormatter = formatter;
    }

    @Input() property: DisplayProperty = {label: '', value: '', valueFormatter: ''};
}
