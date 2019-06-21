import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ComponentDemoRoutingModule } from './component-demo-routing.module';
import { SharedModule } from 'openpos-client-core-lib';
import { PrimaryButtonComponent } from './primary-button/primary-button.component';

@NgModule({
  declarations: [ PrimaryButtonComponent ],
  imports: [
    CommonModule,
    ComponentDemoRoutingModule,
    SharedModule
  ]
})
export class ComponentDemoModule { }
