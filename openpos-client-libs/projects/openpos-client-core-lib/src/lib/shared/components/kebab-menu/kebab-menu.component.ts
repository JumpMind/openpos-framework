import { KeyPressProvider } from './../../providers/keypress.provider';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Subscription } from 'rxjs';
import { Configuration } from '../../../configuration/configuration';

@Component({
    selector: 'app-kebab-menu',
    templateUrl: './kebab-menu.component.html',
    styleUrls: ['./kebab-menu.component.scss']
})
export class KebabMenuComponent implements OnDestroy {

    protected subscriptions: Subscription = new Subscription();

    constructor(@Inject(MAT_DIALOG_DATA) public data: any,
                public dialogRef: MatDialogRef<KebabMenuComponent>,
                protected keyPresses: KeyPressProvider) {

        if (Configuration.enableKeybinds) {
            this.data.menuItems.forEach(item => {
                if (!!item.keybind) {
                    this.subscriptions.add(
                        this.keyPresses.subscribe(item.keybind, 100, event => {
                            // ignore repeats
                            if (event.repeat || !Configuration.enableKeybinds) {
                                return;
                            }
                            if (event.type === 'keydown') {
                                this.closeMenu(item);
                            }
                        })
                    );
                }
            });
        }
    }

    ngOnDestroy(): void {
        if (this.subscriptions) {
            this.subscriptions.unsubscribe();
        }
    }

    closeMenu(option: any) {
        this.dialogRef.close(option);
    }

}
