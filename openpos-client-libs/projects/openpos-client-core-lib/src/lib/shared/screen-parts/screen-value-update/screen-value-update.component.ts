
import { IScreenValueUpdate } from './screen-value-update.interface';
import { ScreenPartComponent } from '../screen-part';
import { Component, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Configuration } from '../../../configuration/configuration';
import { SystemStatusType } from '../../../core/interfaces/system-status-type.enum';
import { SystemStatusDialogComponent } from '../../components/system-status/system-status-dialog.component';
import { MatDialog } from '@angular/material';

@ScreenPart({
    name: 'screenValueUpdate'
})
export class ScreenValueUpdate extends ScreenPartComponent<IScreenValueUpdate> {

    constructor(protected dialog: MatDialog, injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
        console.log('ScreenValue sees a message.')
    }
}
