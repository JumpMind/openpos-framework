import {Component, EventEmitter, Injector, Input, Output} from '@angular/core';
import {ScreenPartComponent} from "../screen-part";
import {MutableListItemWithLabelComponentInterface} from "./mutable-list-item-with-label.interface";
import {Observable} from "rxjs/internal/Observable";
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";
import {FormGroup} from "@angular/forms";
import {IFormElement} from "../../../core/interfaces/form-field.interface";


@Component({
    selector: 'app-mutable-list-item-with-label',
    templateUrl: './mutable-list-item-with-label.component.html',
    styleUrls: ['./mutable-list-item-with-label.component.scss']})
export class MutableListItemWithLabelComponent extends ScreenPartComponent<MutableListItemWithLabelComponentInterface>{

    @Input()
    formGroup: FormGroup;
    @Input()
    groupIcon: string;
    @Input()
    inputField: IFormElement;
    @Input()
    labelField: IFormElement;
    @Input()
    isFirst: boolean;
    @Input()
    isLast: boolean;
    @Input()
    isOnly: boolean;

    @Output()
    onFieldChanged = new EventEmitter<IFormElement>();
    @Output()
    add = new EventEmitter<IFormElement>();
    @Output()
    remove = new EventEmitter<IFormElement>();

    isMobile: Observable<boolean>;
    constructor(injector: Injector, private media: OpenposMediaService) {
        super(injector);
        this.initIsMobile();
    }
    initIsMobile(): void {
        this.isMobile = this.media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    screenDataUpdated() {
    }
}
