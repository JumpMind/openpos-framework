import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StartupService, DynamicScreenComponent } from 'openpos-client-core-lib';

const routes: Routes = [
    { path: '**', canActivate: [StartupService], component: DynamicScreenComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TestRoutingModule { }
