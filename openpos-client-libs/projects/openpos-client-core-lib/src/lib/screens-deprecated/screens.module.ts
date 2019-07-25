// Angular Includes
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';

// Screens
import { ChangeComponent } from './change/change.component';
import { WaitComponent } from './wait/wait.component';
import { FullPageImageComponent } from './full-page-image/full-page-image.component';
import { PrintPreviewComponent } from './print-preview/print-preview.component';
import { DynamicListDialogComponent } from './dynamic-list/dynamic-list-dialog.component';
import { ScreenConstants } from './screen.constants';
import { DataTableComponent } from './data-table/data-table.component';
import { AutoCompleteAddressComponent } from './auto-complete-address/auto-complete-address.component';
import { TemporarilySharedScreens } from './temporarily-shared-screens.module';
import { ScreenService } from '../core/services/screen.service';
import { DialogService } from '../core/services/dialog.service';
import { IconService } from '../core/services/icon.service';
import { PromptDialogComponent } from './prompt/prompt-dialog.component';
import { PromptComponent } from './prompt/prompt.component';

const screens = [
    ChangeComponent,
    PrintPreviewComponent,
    WaitComponent,
    FullPageImageComponent,
    DataTableComponent,
    AutoCompleteAddressComponent,
    PromptComponent
];

const dialogs = [
    DynamicListDialogComponent,
    PromptDialogComponent
];
/**
 * @ignore
 */
@NgModule({
    entryComponents: [
        ...screens,
        ...dialogs
    ],
    declarations: [
        ...screens,
        ...dialogs
    ],
    imports: [
        SharedModule,
        TemporarilySharedScreens
    ],
    providers: [
    ]
})
export class ScreensModule {
    constructor(screenService: ScreenService, dialogService: DialogService, iconService: IconService) {
        ScreenConstants.screens.forEach((screen) => {
            screenService.addScreen(screen.name, screen.component);
        });

        ScreenConstants.dialogs.forEach((dialog) => {
            dialogService.addDialog(dialog.name, dialog.component);
        });
    }

 }
