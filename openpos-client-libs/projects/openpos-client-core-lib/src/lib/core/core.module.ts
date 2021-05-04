import {AndroidContentProviderPlugin} from './platform-plugins/cordova-plugins/android-content-provider-plugin';
import {BrowserPrinterPlugin} from './platform-plugins/printers/browser-printer.plugin';
import {PRINTERS} from './platform-plugins/printers/printer.service';
import {ConsoleScannerPlugin} from './platform-plugins/scanners/console-scanner/console-scanner.plugin';
import {ScanditScannerCordovaPlugin} from './platform-plugins/scanners/scandit-scanner-cordova/scandit-scanner-cordova.plugin';
import { SessionService } from './services/session.service';
import { PersonalizationStartupTask } from './startup/personalization-startup-task';
import { STARTUP_TASKS, STARTUP_FAILED_COMPONENT } from './services/startup.service';
import { ToastrModule } from 'ngx-toastr';
// Angular Includes
import { NgModule, Injector, Optional, SkipSelf, ErrorHandler } from '@angular/core';
import { Location, LocationStrategy, PathLocationStrategy, DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BreakpointObserver, MediaMatcher } from '@angular/cdk/layout';

import { SharedModule } from '../shared/shared.module';
import { AppInjector } from './app-injector';

import { throwIfAlreadyLoaded } from './module-import-guard';
import { StartupComponent } from './startup/startup.component';
import { PersonalizationService } from './personalization/personalization.service';
import { ConfigurationService } from './services/configuration.service';
import { DialogService } from './services/dialog.service';
import { ErrorHandlerService } from './services/errorhandler.service';
import { StompRService } from '@stomp/ng2-stompjs';
import { SubscribeToSessionTask } from './startup/subscribe-to-session-task';
import { Router } from '@angular/router';
import { StartupFailedComponent } from './startup/startup-failed.component';
import { MatDialog } from '@angular/material';
import { FinalStartupTask } from './startup/final-startup-task';
import { DialogContentComponent } from './components/dialog-content/dialog-content.component';
import { DialogServiceStartupTask } from './startup/dialog-service-startup-task';
import { TrainingOverlayService } from './services/training-overlay.service';
import { KeyPressProvider } from '../shared/providers/keypress.provider';
import { fromEvent, Observable } from 'rxjs';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { PersonalizationComponent } from './personalization/personalization.component';
import { ToastService } from './services/toast.service';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxElectronModule } from 'ngx-electron';
import { PluginStartupTask, PLUGINS } from './startup/plugin-startup-task';
import { AilaScannerCordovaPlugin } from './platform-plugins/scanners/aila-scanner-cordova/aila-scanner-cordova.plugin';
import { SCANNERS, ScannerService } from './platform-plugins/scanners/scanner.service';
import { PlatformReadyStartupTask, PLATFORMS } from './startup/platform-ready-startup-task';
import { WedgeScannerPlugin } from './platform-plugins/scanners/wedge-scanner/wedge-scanner.plugin';
import { CordovaPlatform } from './platforms/cordova.platform';
import { InfineaScannerCordovaPlugin } from './platform-plugins/scanners/infinea-scanner/infinea-scanner-cordova/infinea-scanner-cordova.plugin';
import { NCRPaymentPlugin } from './platform-plugins/cordova-plugins/ncr-payment-plugin';
import { SplashScreenComponent } from './components/splash-screen/splash-screen.component';
import { ScanditCameraViewComponent } from './platform-plugins/scanners/scandit-scanner-cordova/scandit-camera-view/scandit-camera-view.component';
import { LockScreenComponent } from './lock-screen/lock-screen.component';

// Add supported locales
import { registerLocaleData } from '@angular/common';
import locale_enCA from '@angular/common/locales/en-CA';
import locale_frCA from '@angular/common/locales/fr-CA';
import { LocationService, PROVIDERS } from './services/location.service';
import { LocationProviderDefault } from './location-providers/location-provider-default';
import { ConsoleIntercepter, LOGGERS } from './logging/console-interceptor.service';
import { ServerLogger } from './logging/server-logger.service';
import { CLIENTCONTEXT } from './client-context/client-context-provider.interface';
import { TimeZoneContext } from './client-context/time-zone-context';
import {UIDataMessageService} from './ui-data-message/ui-data-message.service';
import { HelpTextService } from './help-text/help-text.service';
import {ErrorStateMatcher, ShowOnDirtyErrorStateMatcher} from "@angular/material/core";
import { ServerScannerPlugin } from './platform-plugins/scanners/server-scanner/server-scanner.service';
import {TransactionService} from './services/transaction.service';
import { AudioStartupTask } from './audio/audio-startup-task';
import { AudioService } from './audio/audio.service';
import { AudioRepositoryService } from './audio/audio-repository.service';
import { AudioInteractionService } from './audio/audio-interaction.service';
import { AudioConsolePlugin } from './audio/audio-console.plugin';
import {ClientExecutableService} from "./services/client-executable.service";
import {InfineaScannerCapacitorPlugin} from "./platform-plugins/scanners/infinea-scanner/infinea-scanner-capacitor/infinea-scanner-capacitor.plugin";
import {Dpp255CapacitorPlugin} from "./platform-plugins/printers/dpp-255-capacitor.plugin";
import {AutoPersonalizationStartupTask} from "./startup/auto-personalization-startup-task";

registerLocaleData(locale_enCA, 'en-CA');
registerLocaleData(locale_frCA, 'fr-CA');

@NgModule({
    entryComponents: [
        ConfirmationDialogComponent,
        PersonalizationComponent,
        StartupComponent,
        StartupFailedComponent,
        DialogContentComponent,
        SplashScreenComponent,
        ScanditCameraViewComponent,
        LockScreenComponent
    ],
    declarations: [
        DialogContentComponent,
        ConfirmationDialogComponent,
        PersonalizationComponent,
        StartupComponent,
        StartupFailedComponent,
        SplashScreenComponent,
        ScanditCameraViewComponent,
        LockScreenComponent
    ],
    imports: [
        SharedModule,
        BrowserModule,
        BrowserAnimationsModule,
        NgxElectronModule,
        ToastrModule.forRoot()
    ],
    exports: [
        BrowserModule,
        BrowserAnimationsModule,
        ToastrModule
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
        { provide: STARTUP_TASKS, useClass: AutoPersonalizationStartupTask, multi: true, deps: [PersonalizationService, MatDialog]},
        { provide: STARTUP_TASKS, useClass: PersonalizationStartupTask, multi: true, deps: [PersonalizationService, MatDialog]},
        { provide: STARTUP_TASKS, useClass: SubscribeToSessionTask, multi: true, deps: [SessionService, Router]},
        { provide: STARTUP_TASKS, useClass: DialogServiceStartupTask, multi: true, deps: [DialogService]},
        { provide: STARTUP_TASKS, useClass: AudioStartupTask, multi: true, deps: [AudioRepositoryService, AudioService, AudioInteractionService]},
        { provide: STARTUP_TASKS, useClass: FinalStartupTask, multi: true, deps: [SessionService]},
        { provide: STARTUP_TASKS, useClass: PlatformReadyStartupTask, multi: true },
        { provide: STARTUP_TASKS, useClass: PluginStartupTask, multi: true },
        { provide: STARTUP_FAILED_COMPONENT, useValue: StartupFailedComponent},
        AilaScannerCordovaPlugin,
        ScanditScannerCordovaPlugin,
        { provide: SCANNERS, useExisting: AilaScannerCordovaPlugin, multi: true},
        { provide: SCANNERS, useExisting: WedgeScannerPlugin, multi: true },
        { provide: SCANNERS, useExisting: InfineaScannerCordovaPlugin, multi: true},
        { provide: SCANNERS, useExisting: ConsoleScannerPlugin, multi: true},
        { provide: SCANNERS, useExisting: ScanditScannerCordovaPlugin, multi: true},
        { provide: SCANNERS, useExisting: InfineaScannerCapacitorPlugin, multi: true },
        { provide: PLUGINS, useExisting: AilaScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: InfineaScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: NCRPaymentPlugin, multi: true, deps: [SessionService]},
        { provide: PLUGINS, useExisting: AndroidContentProviderPlugin, multi: true },
        { provide: PLUGINS, useExisting: ScanditScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: InfineaScannerCapacitorPlugin, multi: true },
        { provide: PLUGINS, useExisting: Dpp255CapacitorPlugin, multi: true },
        { provide: SCANNERS, useExisting: ServerScannerPlugin, multi: true, deps: [SessionService]},
        { provide: PLATFORMS, useExisting: CordovaPlatform, multi: true},
        BrowserPrinterPlugin,
        { provide: PRINTERS, useExisting: BrowserPrinterPlugin, multi: true},
        { provide: PRINTERS, useExisting: Dpp255CapacitorPlugin, multi: true},

        LocationService,
        { provide: PROVIDERS, useExisting: LocationProviderDefault, multi: true},
        TrainingOverlayService,
        ConfigurationService,
        KeyPressProvider,
        { provide: LOGGERS, useExisting: ServerLogger, multi: true, deps: [HttpClient, PersonalizationService, ConsoleIntercepter] },
        HelpTextService,
        { provide: CLIENTCONTEXT, useClass: TimeZoneContext, multi: true },
        { provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher },
        TransactionService,
        AudioService,
        AudioInteractionService,
        AudioRepositoryService,
        { provide: PLUGINS, useExisting: AudioConsolePlugin, multi: true, deps: [AudioService]}
    ]
})
export class CoreModule {

    constructor(@Optional() @SkipSelf() parentModule: CoreModule,
                private injector: Injector,
                logger: ConsoleIntercepter,
                toastService: ToastService,
                uiDataService: UIDataMessageService,
                clientExecutableService: ClientExecutableService,
                keyProvider: KeyPressProvider) {
        throwIfAlreadyLoaded(parentModule, 'CoreModule');
        AppInjector.Instance = this.injector;
        keyProvider.registerKeyPressSource(fromEvent(document, 'keydown') as Observable<KeyboardEvent>);
        keyProvider.registerKeyPressSource(fromEvent(document, 'keyup') as Observable<KeyboardEvent>);
    }
}
