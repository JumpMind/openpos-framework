import { Component, Input, OnInit, Output, EventEmitter, Optional, Inject, ViewChild, AfterViewInit, ElementRef } from '@angular/core';
import { DeviceService } from '../../../services/device.service';
import { SessionService } from '../../../services/session.service';
import { MatDialogRef, MAT_DIALOG_DATA, MatInput } from '@angular/material';
import { Subscription } from 'rxjs/Subscription';
import { IScan } from '../../../templates/sell/isell-template';

@Component({
  selector: 'app-scan-something',
  templateUrl: './scan-something.component.html',
  styleUrls: ['./scan-something.component.scss']
})
export class ScanSomethingComponent implements AfterViewInit {

  @ViewChild(MatInput)
  input: MatInput;

  @Input()
  scanSomethingData: IScan;

  public barcode: string;

  constructor(private session: SessionService, public devices: DeviceService,
    @Optional() public dialogRef: MatDialogRef<ScanSomethingComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: IScan) {

    this.session.subscribeForScreenUpdates((screen: any): void => this.focusFirst());

    if (data) {
      this.scanSomethingData = data;
    }
  }

  public onEnter(): void {
    if (this.barcode && this.barcode.trim().length >= this.scanSomethingData.scanMinLength) {
      this.session.onAction('Next', this.barcode);
      this.barcode = '';
      if (this.dialogRef) {
        this.dialogRef.close();
      }
    }
  }


  ngAfterViewInit(): void {
    this.focusFirst();
  }

  private focusFirst(): void {
    if (this.scanSomethingData && this.scanSomethingData.autoFocusOnScan) {
      this.input.focus();
    }
  }


  private filterBarcodeValue(val: string): string {
    if (!val) {
      return val;
    }
    // Filter out extra characters permitted by HTML5 input type=number (for exponentials)
    const pattern = /[e|E|\+|\-|\.]/g;

    return val.toString().replace(pattern, '');
  }

  onBarcodePaste(event: ClipboardEvent) {
    const content = event.clipboardData.getData('text/plain');
    const filteredContent = this.filterBarcodeValue(content);
    if (filteredContent !== content) {
      console.log(`Clipboard data contains invalid characters for barcode, suppressing pasted content '${content}'`);
    }
    return filteredContent === content;
  }
}
