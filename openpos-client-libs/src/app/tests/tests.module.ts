import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule, ScreensWithPartsModule } from 'openpos-client-core-lib';
import { TestRoutingModule } from './tests-routing.module';
import { TestRootComponent } from './test-root.component';

@NgModule({
  declarations: [ TestRootComponent ],
  imports: [
    CommonModule,
    ScreensWithPartsModule,
    TestRoutingModule,
    SharedModule
  ]
})
export class TestModule { }
