import { Component, Input, Optional, Inject, ViewChild, AfterViewInit, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatInput } from '@angular/material';
import { Subscription } from 'rxjs';
import { IMessageHandler } from '../../../core/interfaces/message-handler.interface';
import { IScan } from '../../../screens-deprecated/templates/sell-template/sell/scan.interface';
import { Logger } from '../../../core/services/logger.service';
import { SessionService } from '../../../core/services/session.service';
import { DeviceService } from '../../../core/services/device.service';
import { ActionService } from '../../../core/actions/action.service';
import { OnBecomingActive } from '../../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../../core/life-cycle-interfaces/leaving-active.interface';
import { ScannerService } from '../../../core/platform-plugins/scanners/scanner.service';

@Component({
  selector: 'app-scan-something',
  templateUrl: './scan-something.component.html',
  styleUrls: ['./scan-something.component.scss']
})
export class ScanSomethingComponent implements AfterViewInit, IMessageHandler<any>, OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

  @ViewChild(MatInput)
  input: MatInput;

  @Input()
  scanSomethingData: IScan;

  public barcode: string;

  private subscription: Subscription;

  private scanServiceSubscription: Subscription;

  constructor(
    private log: Logger, private session: SessionService, public devices: DeviceService,
    private actionService: ActionService,
    @Optional() public dialogRef: MatDialogRef<ScanSomethingComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: IScan, private scannerService: ScannerService) {

    this.subscription = this.session.registerMessageHandler(this, 'Screen');

    if (data) {
      this.scanSomethingData = data;
    }
  }

  handle(message: any) {
    if (message.template && !message.template.dialog) {
        this.focusFirst();
    }
  }

  ngOnInit(): void {
    this.registerScanner();
  }

  onBecomingActive() {
    this.registerScanner();
  }

  onLeavingActive() {
    this.unregisterScanner();
  }

  private registerScanner() {
    if (typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null) {
      this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
        this.actionService.doAction({action: this.scanSomethingData.scanActionName}, scanData.data);
      });
    }
  }

  private unregisterScanner() {
    if (this.scanServiceSubscription) {
      this.scanServiceSubscription.unsubscribe();
      this.scanServiceSubscription = null;
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.subscription = null;
    this.unregisterScanner();
    this.scannerService.stopScanning();
  }

  public onEnter(): void {
    if (this.barcode && this.barcode.trim().length >= this.scanSomethingData.scanMinLength) {
      this.actionService.doAction({action: 'Next'}, this.barcode);
      this.barcode = '';
      if (this.dialogRef) {
        this.dialogRef.close();
      }
    }
  }


  ngAfterViewInit(): void {
    setTimeout(() => this.focusFirst());
  }

  private focusFirst(): void {
    if (this.scanSomethingData && this.scanSomethingData.autoFocusOnScan) {
      this.input.focus();
    }
  }
}
