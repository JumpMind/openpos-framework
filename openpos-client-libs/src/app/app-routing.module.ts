import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
    {
        path: 'componentdemo',
        loadChildren: './component-demo/component-demo.module#ComponentDemoModule'
    },
    {
        path: 'tests',
        loadChildren: './tests/tests.module#TestModule'
    }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: true, useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
