import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

import {
  CoreModule,
  SharedModule,
  PersonalizationService,
  ClientUrlService,
  ConfigurationService,
} from '@jumpmind/openpos-client-core-lib';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { NotFoundComponent } from './components/not-found.component';

@NgModule({
  entryComponents: [
  ],
  declarations: [
    AppComponent,
    NotFoundComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CoreModule,
    SharedModule
  ],
  providers: [
    { provide: LocationStrategy, useClass: HashLocationStrategy }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(clientUrlService: ClientUrlService, personalization: PersonalizationService, configuration: ConfigurationService) {
    clientUrlService.navigateExternal = true;
  }
}
