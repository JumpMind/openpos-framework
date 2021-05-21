import {AndroidContentProviderPlugin} from './platform-plugins/cordova-plugins/android-content-provider-plugin';
import {BrowserPrinterPlugin} from './platform-plugins/printers/browser-printer.plugin';
import {PRINTERS} from './platform-plugins/printers/printer.service';
import {ConsoleScannerPlugin} from './platform-plugins/barcode-scanners/console-scanner/console-scanner.plugin';
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
import { ElectronService, NgxElectronModule } from 'ngx-electron';
import { PluginStartupTask, PLUGINS } from './startup/plugin-startup-task';
import { PlatformReadyStartupTask, PLATFORMS } from './startup/platform-ready-startup-task';
import { CordovaPlatform } from './platforms/cordova.platform';
import { NCRPaymentPlugin } from './platform-plugins/cordova-plugins/ncr-payment-plugin';
import { SplashScreenComponent } from './components/splash-screen/splash-screen.component';
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
import {TransactionService} from './services/transaction.service';
import { AudioStartupTask } from './audio/audio-startup-task';
import { AudioService } from './audio/audio.service';
import { AudioRepositoryService } from './audio/audio-repository.service';
import { AudioInteractionService } from './audio/audio-interaction.service';
import { AudioConsolePlugin } from './audio/audio-console.plugin';
import { CapacitorStatusBarPlatformPlugin } from './startup/capacitor-status-bar-platform-plugin';
import { ScanditCapacitorImageScanner } from './platform-plugins/barcode-scanners/scandit-capacitor/scandit-capacitor.service';
import { IMAGE_SCANNERS, SCANNERS } from './platform-plugins/barcode-scanners/scanner';
import { BarcodeScanner } from './platform-plugins/barcode-scanners/barcode-scanner.service';
import {ClientExecutableService} from "./services/client-executable.service";
import { CapacitorIosPlatform } from './platforms/capacitor-ios.platform';
import { CapacitorAndroidPlatform } from './platforms/capacitor-android.platform';
import { AilaScannerCordovaPlugin } from './platform-plugins/barcode-scanners/aila-scanner-cordova/aila-scanner-cordova.plugin';
import { InfineaScannerCordovaPlugin } from './platform-plugins/barcode-scanners/infinea-scanner-cordova/infinea-scanner-cordova.plugin';
import { WedgeScannerPlugin } from './platform-plugins/barcode-scanners/wedge-scanner/wedge-scanner.plugin';
import { ServerScannerPlugin } from './platform-plugins/barcode-scanners/server-scanner/server-scanner.service';
import { ScanditScannerCordovaPlugin } from './platform-plugins/barcode-scanners/scandit-scanner-cordova/scandit-scanner-cordova.plugin';
import { CapacitorStorageService } from './storage/capacitor/capacitor-storage.service';
import { Storage } from './storage/storage.service';
import { STORAGE_CONTAINERS } from './storage/storage-container';
import { CapacitorPrinterPlugin } from './platform-plugins/printers/capacitor-printer.plugin';
import { AutoPersonalizationStartupTask } from "./startup/auto-personalization-startup-task";
import { ZEROCONF_TOKEN } from './startup/zeroconf/zeroconf';
import { MDnsZeroconf } from './startup/zeroconf/mdns-zeroconf';
import { CapacitorZeroconf } from './startup/zeroconf/capacitor-zeroconf';
import { CapacitorService } from './services/capacitor.service';

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
        LockScreenComponent,
    ],
    declarations: [
        DialogContentComponent,
        ConfirmationDialogComponent,
        PersonalizationComponent,
        StartupComponent,
        StartupFailedComponent,
        SplashScreenComponent,
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
        BarcodeScanner,
        { provide: ZEROCONF_TOKEN, useClass: MDnsZeroconf, multi: true, deps: [ElectronService] },
        { provide: ZEROCONF_TOKEN, useClass: CapacitorZeroconf, multi: true, deps: [CapacitorService] },
        { provide: STARTUP_TASKS, useClass: AutoPersonalizationStartupTask, multi: true, deps: [PersonalizationService, MatDialog, ZEROCONF_TOKEN]},
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
        { provide: SCANNERS, useExisting: ConsoleScannerPlugin, multi: true },
        { provide: SCANNERS, useExisting: AilaScannerCordovaPlugin, multi: true},
        { provide: SCANNERS, useExisting: WedgeScannerPlugin, multi: true },
        { provide: SCANNERS, useExisting: InfineaScannerCordovaPlugin, multi: true},
        { provide: SCANNERS, useExisting: ServerScannerPlugin, multi: true, deps: [SessionService]},
        { provide: IMAGE_SCANNERS, useExisting: ScanditScannerCordovaPlugin, multi: true},
        { provide: IMAGE_SCANNERS, useExisting: ScanditCapacitorImageScanner, multi: true },
        { provide: PLUGINS, useExisting: AilaScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: InfineaScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: NCRPaymentPlugin, multi: true, deps: [SessionService]},
        { provide: PLUGINS, useExisting: AndroidContentProviderPlugin, multi: true },
        { provide: PLUGINS, useExisting: ScanditScannerCordovaPlugin, multi: true},
        { provide: PLUGINS, useExisting: CapacitorStatusBarPlatformPlugin, multi: true },
        { provide: PLUGINS, useExisting: ScanditCapacitorImageScanner, multi: true },
        { provide: PLATFORMS, useExisting: CordovaPlatform, multi: true},
        { provide: PLATFORMS, useExisting: CapacitorIosPlatform, multi: true },
        { provide: PLATFORMS, useExisting: CapacitorAndroidPlatform, multi: true },
        { provide: STORAGE_CONTAINERS, useClass: CapacitorStorageService, multi: true },
        CapacitorPrinterPlugin,
        { provide: PRINTERS, useExisting: CapacitorPrinterPlugin, multi: true},
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
        { provide: PLUGINS, useExisting: AudioConsolePlugin, multi: true, deps: [AudioService]},
        Storage
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
