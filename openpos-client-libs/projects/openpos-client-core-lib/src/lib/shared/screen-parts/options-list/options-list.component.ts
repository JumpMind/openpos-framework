import { Component, Output, EventEmitter, Injector, Input } from '@angular/core';
import {ActionService} from '../../../core/actions/action.service';
import { OptionsListInterface } from './options-list.interface';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Observable } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';
import { FocusService } from '../../../core/focus/focus.service';
import { MatDialog } from '@angular/material';
import { KebabMenuComponent } from '../../components/kebab-menu/kebab-menu.component';

@ScreenPart({
    name: 'optionsList'
})
@Component({
    selector: 'app-options-list',
    templateUrl: './options-list.component.html',
    styleUrls: ['./options-list.component.scss']
})
export class OptionsListComponent extends ScreenPartComponent<OptionsListInterface> {

    @Output()
    optionClick = new EventEmitter<IActionItem>();

    @Input()
    listSize = -1;

    @Input()
    optionListSizeClass = 'lg';

    options: IActionItem[] = [];
    overflowOptions: IActionItem[] = [];

    isMobile: Observable<boolean>;

    constructor( injector: Injector, mediaService: OpenposMediaService, protected dialog: MatDialog,
                 protected focusService: FocusService) {

        super(injector);
        this.isMobile = mediaService.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    screenDataUpdated() {
        if (this.listSize > 0 && this.screenData.options && this.listSize < this.screenData.options.length) {
            this.options = [];
            this.overflowOptions = [];
            for (let i = 0; i < this.screenData.options.length; i++) {
                if (i < this.listSize - 1) {
                    this.options.push(this.screenData.options[i]);
                } else {
                    this.overflowOptions.push(this.screenData.options[i]);
                }
            }
        } else {
            this.overflowOptions = [];
            this.options = this.screenData.options;
        }
    }

    onOptionClick(actionItem: IActionItem): void {
        if ( this.optionClick.observers.length > 0 ) {
            this.optionClick.emit(actionItem);
        } else {
            this.doAction(actionItem);
        }
    }

    public openKebabMenu() {
        if (this.dialog.openDialogs.length < 1 && !this.actionService.actionBlocked()) {
            const dialogRef = this.dialog.open(KebabMenuComponent, {
                data: {
                    menuItems: this.overflowOptions,
                    payload: null,
                    disableClose: false,
                    autoFocus: false,
                    restoreFocus: false
                },
                autoFocus: false
            });

            this.subscriptions.add(dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.optionClick.emit(result);
                }
                this.focusService.restoreInitialFocus();
            }));
        }
    }
}
