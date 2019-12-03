import { Component, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Subscription } from 'rxjs';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';
import { KebabMenuComponent } from '../kebab-menu/kebab-menu.component';
import { FocusService } from '../../../core/focus/focus.service';
import { IActionItem } from '../../../core/actions/action-item.interface';

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

    protected subscription: Subscription;

    constructor(protected dialog: MatDialog, protected keyPresses: KeyPressProvider, protected focusService: FocusService) {
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    public openKebabMenu() {
        if (this.dialog.openDialogs.length < 1) {
            const dialogRef = this.dialog.open(KebabMenuComponent, {
                data: {
                    menuItems: this.menuItems,
                    payload: null,
                    disableClose: false,
                    autoFocus: false,
                    restoreFocus: false
                },
                autoFocus: false
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.menuItemClick.emit(result);
                }
                this.focusService.restoreInitialFocus();
            });
        }
    }
}
