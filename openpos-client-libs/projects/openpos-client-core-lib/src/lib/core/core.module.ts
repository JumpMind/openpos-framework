import { DevMenuComponent } from './components/dev-menu/dev-menu.component';
import { SessionService } from './services/session.service';
import { PersonalizationStartupTask } from './components/startup/personalization-startup-task';
import { STARTUP_TASKS, STARTUP_COMPONENT, STARTUP_FAILED_COMPONENT } from './services/startup.service';

// Angular Includes
import { NgModule, Injector, Optional, SkipSelf, ErrorHandler } from '@angular/core';
import { Location, LocationStrategy, PathLocationStrategy, DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BreakpointObserver, MediaMatcher } from '@angular/cdk/layout';

import { SharedModule } from '../shared/shared.module';
import { AppInjector } from './app-injector';

import { throwIfAlreadyLoaded } from './module-import-guard';
import { StartupComponent } from './components/startup/startup.component';
import { PersonalizationService } from './services/personalization.service';
import { ConfigurationService } from './services/configuration.service';
import { DialogService } from './services/dialog.service';
import { ErrorHandlerService } from './services/errorhandler.service';
import { StompRService } from '@stomp/ng2-stompjs';
import { SubscribeToSessionTask } from './components/startup/subscribe-to-session-task';
import { Router, RouterModule } from '@angular/router';
import { Logger } from './services/logger.service';
import { StartupFailedComponent } from './components/startup/startup-failed.component';
import { MatDialog } from '@angular/material';
import { FinalStartupTask } from './components/startup/final-startup-task';
import { DialogContentComponent } from './components/dialog-content/dialog-content.component';
import { DialogServiceStartupTask } from './components/startup/dialog-service-startup-task';
import { TrainingOverlayService } from './services/training-overlay.service';
import { KeyPressProvider } from '../shared/providers/keypress.provider';
import { fromEvent, Observable } from 'rxjs';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { PersonalizationComponent } from './components/personalization/personalization.component';
import { OpenposAppComponent } from './components/openpos-app/openpos-app.component';
import { DynamicScreenComponent } from './components/dynamic-screen/dynamic-screen.component';
import { LoaderComponent } from './components/loader/loader.component';
import { StatusBarStatusControlComponent } from '../shared/components/status-bar-status-control/status-bar-status-control.component';
import { STATUS_BAR_STATUS_CONTROL_COMPONENT } from '../shared/components/status-bar/status-bar.component';
import { ToastService } from './services/toast.service';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxElectronModule } from 'ngx-electron';
import { PluginStartupTask, PLUGINS } from './components/startup/plugin-startup-task';
import { AilaScannerCordovaPlugin } from './plugins/aila-scanner-cordova.plugin';
import { SCANNERS, ScannerService } from './services/scanner.service';
import { PlatformReadyStartupTask, PLATFORMS } from './components/startup/platform-ready-startup-task';
import { WedgeScannerPlugin } from './plugins/wedge-scanner.plugin';
import { CordovaPlatform } from './platforms/cordova.platform';
import { InfineaScannerCordovaPlugin } from './plugins/infinea-scanner-cordova.plugin';
import { SplashScreenComponent } from './components/splash-screen/splash-screen.component';

// Add supported locales
import { registerLocaleData } from '@angular/common';
import locale_enCA from '@angular/common/locales/en-CA';
import locale_frCA from '@angular/common/locales/fr-CA';
import { LocationService, PROVIDERS } from './services/location.service';
import { LocationProviderDefault } from './location-providers/location-provider-default';

registerLocaleData(locale_enCA, 'en-CA');
registerLocaleData(locale_frCA, 'fr-CA');

@NgModule({
    entryComponents: [
        ConfirmationDialogComponent,
        PersonalizationComponent,
        StartupComponent,
        StartupFailedComponent,
        DialogContentComponent,
        StatusBarStatusControlComponent,
        SplashScreenComponent
    ],
    declarations: [
        OpenposAppComponent,
        DynamicScreenComponent,
        DevMenuComponent,
        DialogContentComponent,
        LoaderComponent,
        ConfirmationDialogComponent,
        PersonalizationComponent,
        StartupComponent,
        StartupFailedComponent,
        StatusBarStatusControlComponent,
        SplashScreenComponent
    ],
    imports: [
        SharedModule,
        RouterModule,
        BrowserModule,
        BrowserAnimationsModule,
        NgxElectronModule
    ],
    exports: [
        LoaderComponent,
        OpenposAppComponent,
        BrowserModule,
        BrowserAnimationsModule
    ],
    providers: [
        HttpClient,
        Location,
        { provide: LocationStrategy, useClass: PathLocationStrategy },
        { provide: ErrorHandler, useClass: ErrorHandlerService },
        DatePipe,
        BreakpointObserver,
        MediaMatcher,
        StompRService,
        ScannerService,
        { provide: STARTUP_TASKS, useClass: PersonalizationStartupTask, multi: true, deps: [PersonalizationService, MatDialog]},
        { provide: STARTUP_TASKS, useClass: SubscribeToSessionTask, multi: true, deps: [SessionService, Router, Logger]},
        { provide: STARTUP_TASKS, useClass: DialogServiceStartupTask, multi: true, deps: [DialogService]},
        { provide: STARTUP_TASKS, useClass: FinalStartupTask, multi: true, deps: [SessionService]},
        { provide: STARTUP_TASKS, useClass: PlatformReadyStartupTask, multi: true },
        { provide: STARTUP_TASKS, useClass: PluginStartupTask, multi: true },
        { provide: STARTUP_COMPONENT, useValue: StartupComponent },
        { provide: STARTUP_FAILED_COMPONENT, useValue: StartupFailedComponent},
        AilaScannerCordovaPlugin,
        { provide: SCANNERS, useExisting: AilaScannerCordovaPlugin, multi: true},
        { provide: SCANNERS, useExisting: WedgeScannerPlugin, multi: true },
        { provide: SCANNERS, useExisting: InfineaScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: AilaScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: InfineaScannerCordovaPlugin, multi: true},
        { provide: PLATFORMS, useExisting: CordovaPlatform, multi: true},
        LocationService,
        { provide: PROVIDERS, useExisting: LocationProviderDefault, multi: true},
        TrainingOverlayService,
        ConfigurationService,
        KeyPressProvider,
        { provide: STATUS_BAR_STATUS_CONTROL_COMPONENT, useValue: StatusBarStatusControlComponent }
    ]
})
export class CoreModule {

    constructor(@Optional() @SkipSelf() parentModule: CoreModule,
                private injector: Injector,
                toastService: ToastService,
                keyProvider: KeyPressProvider) {
        throwIfAlreadyLoaded(parentModule, 'CoreModule');
        AppInjector.Instance = this.injector;
        keyProvider.registerKeyPressSource(fromEvent(document, 'keydown') as Observable<KeyboardEvent>);
        keyProvider.registerKeyPressSource(fromEvent(document, 'keyup') as Observable<KeyboardEvent>);
    }
}
