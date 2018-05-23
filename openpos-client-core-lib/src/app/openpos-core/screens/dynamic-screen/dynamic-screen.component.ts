import { FileViewerComponent } from './../../dialogs/file-viewer/file-viewer.component';
import { OpenPOSDialogConfig } from './../../common/idialog';
import { TemplateDirective } from './../../common/template.directive';
import { IScreen } from './../../common/iscreen';
import { IPlugin } from './../../common/iplugin';
import { PluginService } from './../../services/plugin.service';
import { FileUploadService } from './../../services/file-upload.service';
import { DeviceService } from './../../services/device.service';
import { ScreenService } from './../../services/screen.service';
import { IMenuItem } from './../../common/imenuitem';
import { Component, DoCheck, ViewChild, NgZone, HostListener, ComponentRef, OnDestroy, OnInit, ComponentFactory } from '@angular/core';
import { SessionService } from './../../services/session.service';
import { FocusDirective } from './../../common/focus.directive';
import { MatDialog, MatDialogRef, MatSnackBar, MatMenuTrigger, MatSnackBarRef, SimpleSnackBar } from '@angular/material';
import { IconService } from './../../services/icon.service';
import { OverlayContainer } from '@angular/cdk/overlay';
import { Router } from '@angular/router';
import { DialogService } from './../../services/dialog.service';
import { AbstractTemplate } from '../..';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-dynamic-screen',
  templateUrl: './dynamic-screen.component.html',
  styleUrls: ['./dynamic-screen.component.scss']
})
export class DynamicScreenComponent implements OnDestroy, OnInit {

  public backButton: IMenuItem;

  firstClickTime = Date.now();

  clickCount = 0;

  logFilenames: string[];

  logPlugin: IPlugin;

  showDevMenu = false;

  logsAvailable = false;

  private dialogRef: MatDialogRef<IScreen>;

  private previousScreenType: string;

  private dialogOpening: boolean;

  private previousScreenName: string;

  private snackBarRef: MatSnackBarRef<SimpleSnackBar>;

  private personalized: boolean;

  private registered: boolean;

  private installedScreen: IScreen;

  private currentTemplateRef: ComponentRef<IScreen>;

  private installedTemplate: AbstractTemplate;

  private lastDialogType: string;

  public classes = '';

  private currentTheme: string;

  private disableDevMenu = false;

  @ViewChild(TemplateDirective) host: TemplateDirective;

  constructor(public screenService: ScreenService, public dialogService: DialogService, public session: SessionService,
    public deviceService: DeviceService, public dialog: MatDialog,
    public iconService: IconService, public snackBar: MatSnackBar, public overlayContainer: OverlayContainer,
    protected router: Router, private pluginService: PluginService,
    private fileUploadService: FileUploadService,
    private httpClient: HttpClient) {
  }

  ngOnInit(): void {

    const self = this;
    this.session.subscribeForScreenUpdates((screen: any): void => self.updateTemplateAndScreen(screen));
    this.session.subscribeForDialogUpdates((dialog: any): void => self.updateDialog(dialog));

    if (!this.registerWithServer()) {
      this.updateTemplateAndScreen();
    }
  }


  ngOnDestroy(): void {
    this.session.unsubscribe();
  }

  @HostListener('document:click', ['$event'])
  @HostListener('document:touchstart', ['$event'])
  documentClick(event: any) {
    const screenWidth = window.innerWidth;
    let x = event.clientX;
    let y = event.clientY;
    if (event.type === 'touchstart') {
      console.log(event);
      x = event.changedTouches[0].pageX;
      y = event.changedTouches[0].pageY;
    }
    // console.log(`${screenWidth} ${x} ${y}`);
    if (this.clickCount === 0 || Date.now() - this.firstClickTime > 1000 ||
      (y > 100) || this.disableDevMenu) {
      this.firstClickTime = Date.now();
      this.clickCount = 0;
    }

    if (y < 100) {
      this.clickCount = ++this.clickCount;
    }

    if (this.clickCount === 5) {
      this.onDevMenuClick();
      this.clickCount = 0;
    }
  }

  protected onDevMenuClick(): void {
    if (!this.showDevMenu) {
      this.pluginService.getPlugin('openPOSCordovaLogPlugin').then(
        (plugin: IPlugin) => {
          this.logPlugin = plugin;
          if (this.logPlugin && this.logPlugin.impl) {
            this.logsAvailable = true;
            this.logPlugin.impl.listLogFiles(
              (fileNames) => {
                this.logFilenames = fileNames;
              },
              (error) => {
                this.logFilenames = [];
              }
            );
          } else {
            this.logsAvailable = false;
          }
        }
      ).catch(error => {
        this.logsAvailable = false;
      });
    }
    this.showDevMenu = !this.showDevMenu;

  }

  public onDevRefreshView() {
    this.session.refreshApp();
  }

  public onPersonalize() {
    this.session.dePersonalize();
    this.session.showScreen(this.session.getPersonalizationScreen());
  }

  public onDevClearLocalStorage() {
    localStorage.clear();
    this.session.refreshApp();
  }

  public onDevRestartNode(): Promise<{ success: boolean, message: string }> {

    const prom = new Promise<{ success: boolean, message: string }>((resolve, reject) => {
      const port = this.session.getServerPort();
      const nodeId = this.session.getNodeId().toString();
      const url = `http://${this.session.getServerName()}${port ? `:${port}` : ''}` +
        `/register/restart/node/${nodeId}`;
      const httpClient = this.httpClient;
      httpClient.get(url).subscribe(response => {
        const msg = `Node '${nodeId}' restarted successfully.`;
        console.log(msg);
        resolve({ success: true, message: msg });
      },
        err => {
          const msg = `Node restart Error occurred: ${JSON.stringify(err)}`;
          const statusCode = err.status || (err.error ? err.error.status : null);
          let errMsg = '';
          if (err.error) {
            if (err.error.error) {
              errMsg += err.error.error;
            }
            if (err.error.message) {
              errMsg += (errMsg ? '; ' : '') + err.error.message;
            }
          }
          const returnMsg = `${statusCode ? statusCode + ': ' : ''}` +
            (errMsg ? errMsg : 'Restart failed. Check client and server logs.');
          reject({ success: false, message: returnMsg });
        });

    });
    return prom;
  }

  protected onLogfileSelected(logFilename: string): void {
    if (this.logPlugin && this.logPlugin.impl) {
      this.logPlugin.impl.shareLogFile(
        logFilename,
        () => {
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }

  protected onLogfileUpload(logFilename: string): void {
    if (this.logPlugin && this.logPlugin.impl) {
      this.logPlugin.impl.getLogFilePath(
        logFilename,
        (logfilePath) => {
          this.fileUploadService.uploadLocalDeviceFileToServer('log', logFilename, 'text/plain', logfilePath)
            .then((result: { success: boolean, message: string }) => {
              this.snackBar.open(result.message, 'Dismiss', {
                duration: 8000, verticalPosition: 'top'
              });
            })
            .catch((result: { success: boolean, message: string }) => {
              this.snackBar.open(result.message, 'Dismiss', {
                duration: 8000, verticalPosition: 'top'
              });
            });
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }

  protected onLogfileView(logFilename: string): void {
    if (this.logPlugin && this.logPlugin.impl) {
      this.logPlugin.impl.readLogFileContents(
        logFilename,
        (logFileContents) => {
          const dialogRef = this.dialog.open(FileViewerComponent, {
            panelClass: 'full-screen-dialog',
            maxWidth: '100vw', maxHeight: '100vh', width: '100vw'
          });
          dialogRef.componentInstance.fileName = logFilename;
          dialogRef.componentInstance.text = logFileContents;
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }

  public registerWithServer(): boolean {
    if (!this.registered && this.isPersonalized()) {
      console.log('initializing the application');
      this.session.unsubscribe();
      this.session.subscribe(this.normalizeAppIdFromUrl());
      this.registered = true;
    }
    return this.registered;
  }

  protected normalizeAppIdFromUrl(): string {
    let appId = this.router.url.substring(1);
    if (appId.indexOf('#') > 0) {
      appId = appId.substring(0, appId.indexOf('#'));
    }
    if (appId.indexOf('/') > 0) {
      appId = appId.substring(0, appId.indexOf('/'));
    }
    return appId;
  }

  protected isPersonalized(): boolean {
    if (!this.personalized && this.session.isPersonalized()) {
      this.personalized = true;
      console.log('already personalized.  setting needs personalization to false');
    }
    return this.personalized;
  }

  protected updateDialog(dialog?: any): void {
    this.registerWithServer();
    if (dialog) {
      const dialogType = this.dialogService.hasDialog(dialog.subType) ? dialog.subType : 'Dialog';
      if (!this.dialogOpening) {
        if (this.dialogRef && (dialog.type !== this.lastDialogType || dialog.type === 'Dialog')) {
          console.log('closing dialog');
          this.dialogRef.close();
          this.dialogRef = null;
        }
        console.log('opening dialog \'' + dialogType + '\'');
        this.dialogOpening = true;
        setTimeout(() => this.openDialog(dialog), 0);
      } else {
        console.log(`Not opening dialog! Here's why: dialogOpening? ${this.dialogOpening}`);
      }
    } else if (!dialog && this.dialogRef) {
      console.log('closing dialog ref');
      this.dialogRef.close();
      this.dialogRef = null;
    }
  }

  protected updateTemplateAndScreen(screen?: any): void {
    this.registerWithServer();

    if (!this.isPersonalized() && !screen) {
      console.log('setting up the personalization screen');
      screen = this.session.getPersonalizationScreen();
    } else if (!screen) {
      screen = { type: 'Blank', template: { type: 'Blank', dialog: false } };
    }

    if (screen &&
      (screen.refreshAlways
        || screen.type !== this.previousScreenType
        || screen.name !== this.previousScreenName)
    ) {
      this.logSwitchScreens(screen);

      const templateName = screen.template.type;
      const screenType = screen.type;
      const screenName = screen.name;
      const templateComponentFactory: ComponentFactory<IScreen> = this.screenService.resolveScreen(templateName);
      const viewContainerRef = this.host.viewContainerRef;
      viewContainerRef.clear();
      if (this.currentTemplateRef) {
        this.currentTemplateRef.destroy();
      }
      this.currentTemplateRef = viewContainerRef.createComponent(templateComponentFactory);
      this.installedTemplate = this.currentTemplateRef.instance as AbstractTemplate;
      this.previousScreenType = screenType;
      this.previousScreenName = screenName;
      if (this.session.getTheme() !== this.currentTheme) {
        this.overlayContainer.getContainerElement().classList.remove(this.currentTheme);
        this.overlayContainer.getContainerElement().classList.add(this.session.getTheme());
        this.currentTheme = this.session.getTheme();
      }
      this.installedScreen = this.installedTemplate.installScreen(this.screenService.resolveScreen(screenType));
    }
    this.disableDevMenu = screen.template.disableDevMenu;
    this.installedTemplate.show(screen);
    this.installedScreen.show(screen, this, this.installedTemplate);

    this.backButton = screen.backButton;

    this.updateClasses(screen);

  }

  protected logSwitchScreens(screen: any) {
    let msg = `>>> Switching screens from "${this.previousScreenType}" to "${screen.type}"`;
    let nameLogged = false;
    let sequenceLogged = false;
    if (screen.name && screen.name !== screen.type) {
      nameLogged = true;
      msg += ` (name "${screen.name}"`;
    }
    if (screen.sequenceNumber) {
      sequenceLogged = true;
      if (!nameLogged) {
        msg += ` (`;
      } else {
        msg += `, `;
      }
      msg += `sequence ${screen.sequenceNumber})`;
    }
    if (nameLogged && !sequenceLogged) {
      msg += `)`;
    }

    console.log(msg);
  }

  protected updateClasses(screen: any) {
    if (screen) {
      this.classes = '';
      switch (this.session.getAppId()) {
        case 'pos':
          if (screen.type === 'Home') {
            this.classes = 'main-background';
          }
          break;
        case 'selfcheckout':
          if (screen.type === 'SelfCheckoutHome') {
            this.classes = 'main-background selfcheckout';
          } else {
            this.classes = 'selfcheckout';
          }
          break;
        case 'customerdisplay':
          this.classes = 'selfcheckout';
          break;
      }
    }
  }

  protected openDialog(dialog: any) {
    const dialogComponentFactory: ComponentFactory<IScreen> = this.dialogService.resolveDialog(dialog.type);
    let closeable = false;
    if (dialog.template.dialogProperties) {
      closeable = dialog.template.dialogProperties.closeable;
    }
    const dialogProperties: OpenPOSDialogConfig = { disableClose: !closeable, autoFocus: false };
    const dialogComponent = dialogComponentFactory.componentType;
    if (dialog.template.dialogProperties) {
      // Merge in any dialog properties provided on the screen
      for (const key in dialog.template.dialogProperties) {
        if (dialog.template.dialogProperties.hasOwnProperty(key)) {
          dialogProperties[key] = dialog.template.dialogProperties[key];
        }
      }
      console.log(`Dialog options: ${JSON.stringify(dialogProperties)}`);
    }

    if (!this.dialogRef || dialog.type !== this.lastDialogType || dialog.type === 'Dialog'
      || dialog.refreshAlways) {
      this.dialogRef = this.dialog.open(dialogComponent, dialogProperties);
    } else {
      console.log(`Using previously created dialogRef. current dialog type: ${dialog.type}, last dialog type: ${this.lastDialogType}`);
    }

    this.dialogRef.componentInstance.show(dialog, this);
    this.dialogOpening = false;
    console.log('Dialog \'' + dialog.type + '\' opened');
    if (dialogProperties.executeActionBeforeClose) {
      // Some dialogs may need to execute the chosen action before
      // they close so that actionPayloads can be included with the action
      // before the dialog is destroyed.
      this.dialogRef.beforeClose().subscribe(result => {
        this.session.onAction(result);
      });
    }

    this.dialogRef.afterClosed().subscribe(result => {
      if (!dialogProperties.executeActionBeforeClose) {
        this.session.onAction(result);
      }
    }
    );
    this.lastDialogType = dialog.type;
  }

}
