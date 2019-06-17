import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PrimaryButtonComponent } from './primary-button/primary-button.component';

const routes: Routes = [
    { path: 'primarybutton', component: PrimaryButtonComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ComponentDemoRoutingModule { }
