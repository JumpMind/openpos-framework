import {Component, EventEmitter, Input, OnDestroy, Output} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import {Subscription} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {ActionService} from '../../../core/actions/action.service';
import {KeyPressProvider} from '../../providers/keypress.provider';
import {Configuration} from '../../../configuration/configuration';
import {KebabMenuComponent} from '../kebab-menu/kebab-menu.component';
import {FocusService} from '../../../core/focus/focus.service';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";

@Component({
    selector: 'app-kebab-button',
    templateUrl: './kebab-button.component.html',
    styleUrls: ['./kebab-button.component.scss']
})
export class KebabButtonComponent implements OnDestroy {

    @Input()
    menuItems: IActionItem[];

    @Input()
    color?: string;

    @Input()
    iconName = 'KebabMenu';

    @Input()
    iconClass;

    dialogRef: MatDialogRef<KebabMenuComponent>;

    @Input()
    set keyBinding(key: string) {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        // Only subscribe to keypress if key is defined.
        if (!!key) {
            this.subscription = this.keyPresses.subscribe(key, 100, event => {
                // ignore repeats
                if (event.repeat || !Configuration.enableKeybinds) {
                    return;
                }
                if (event.type === 'keydown') {
                    this.openKebabMenu();
                }
            });
        }
    }

    @Output()
    menuItemClick = new EventEmitter<IActionItem>();

    modalWidth: string = '35vw';

    protected subscription: Subscription;

    constructor(protected dialog: MatDialog,
                protected keyPresses: KeyPressProvider,
                protected focusService: FocusService,
                protected actionService: ActionService,
                private mediaService: OpenposMediaService) {
        this.checkScreenSize();
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }

        // Ensure dialog gets closed, if it is still open due
        // to a screen change/refresh while it was open
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    public openKebabMenu() {
        if (this.dialog.openDialogs.length < 1 && !this.actionService.actionBlocked()) {
            this.dialogRef = this.dialog.open(KebabMenuComponent, {
                data: {
                    menuItems: this.menuItems,
                    payload: null,
                    disableClose: false,
                    autoFocus: false,
                    restoreFocus: false
                },
                width: this.modalWidth,
                autoFocus: false
            });

            this.dialogRef.afterClosed().pipe(finalize(() => this.dialogRef = undefined)).subscribe(result => {
                if (result) {
                    this.menuItemClick.emit(result);
                }
                this.focusService.restoreInitialFocus();
            });
        }
    }

    checkScreenSize() {
        this.mediaService.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, "90vw"],
            [MediaBreakpoints.MOBILE_LANDSCAPE, "50vw"],
            [MediaBreakpoints.TABLET_PORTRAIT, "70vw"],
            [MediaBreakpoints.TABLET_LANDSCAPE, "50vw"],
            [MediaBreakpoints.DESKTOP_PORTRAIT, "50vw"],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, "35vw"]
        ])).subscribe(res => this.modalWidth = res);
    }
}