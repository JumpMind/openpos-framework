import {Component, Injector} from '@angular/core';
import {DialogComponent} from '../../shared/decorators/dialog-component.decorator';
import {PosScreen} from '../pos-screen/pos-screen.component';
import {ErrorDialogInterface} from "./error-dialog.interface";

declare var Runner: any;

@DialogComponent({
    name: 'ErrorDialog'
})
@Component({
    selector: 'app-error-dialog',
    templateUrl: './error-dialog.component.html',
    styleUrls: ['./error-dialog.component.scss']
})
export class ErrorDialogComponent extends PosScreen<ErrorDialogInterface> {

    constructor(injector: Injector) {
        super(injector);
    }

    buildScreen() {
        new Runner('.interstitial-wrapper');
    }


    // Copyright (c) 2014 The Chromium Authors. All rights reserved.
    // Use of this source code is governed by a BSD-style license that can be
    // found in the LICENSE file.


}
