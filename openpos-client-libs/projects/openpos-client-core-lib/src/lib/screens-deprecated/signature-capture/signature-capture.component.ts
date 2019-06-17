import { Component, AfterViewInit, HostListener } from '@angular/core';
import 'signature_pad';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { MatSnackBar } from '@angular/material';
import { Configuration } from '../../configuration/configuration';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';


/**
 * @ignore
 */
@ScreenComponent({
    name: 'SignatureCapture'
})
@Component({
  selector: 'app-signature-capture',
  templateUrl: './signature-capture.component.html',
  styleUrls: ['./signature-capture.component.scss']
})
export class SignatureCaptureComponent extends PosScreen<any> implements AfterViewInit {

  static readonly DEFAULT_MEDIA_TYPE = 'image/jpeg';

  protected initialized = false;
  protected signaturePad: SignaturePad;
  protected canvas: HTMLCanvasElement = null;
  protected wrapper: HTMLElement;

  constructor(public snackBar: MatSnackBar) {
      super();
  }

  buildScreen() {}

  ngAfterViewInit(): void {
    this.initialized = true;
    this.wrapper = document.getElementById('signature-pad');
    this.canvas = this.wrapper.querySelector('canvas');
    this.canvas.height = this.wrapper.clientHeight;
    this.canvas.width = this.wrapper.clientWidth;
    this.signaturePad = new SignaturePad(this.canvas);
    this.onResizeCanvas(null);
  }

  @HostListener('window:resize', ['$event'])
  onResizeCanvas(evt: Event) {

      const newWidth = this.wrapper.clientWidth;
      const newHeight = this.wrapper.clientHeight;

      const tempCanvas = document.createElement('canvas');
      const tempContext = tempCanvas.getContext('2d');
      const canvasContext = this.canvas.getContext('2d');

      tempCanvas.width = newWidth;
      tempCanvas.height = newHeight;
      tempContext.fillStyle = 'white'; // TODO: this.canvas.getAttribute('backgroundColor');
      tempContext.fillRect(0, 0, newWidth, newHeight);
      tempContext.drawImage(this.canvas, 0, 0);

      // Don't resize original canvas until after the image has been copied
      this.canvas.width = newWidth;
      this.canvas.height = newHeight;

      canvasContext.drawImage(tempCanvas, 0, 0);
  }

  onClearSignature(): void {
    this.signaturePad.clear();
  }

  onSaveSignature(): void {
    if ( this.signaturePad.isEmpty()) {
      this.log.info('Signature is empty');
      return;
    }
    const mediaType: string = this.screen.signatureMediaType ?
        this.screen.signatureMediaType : SignatureCaptureComponent.DEFAULT_MEDIA_TYPE;

    const dataUrl: string|null = this.signaturePad.toDataURL(mediaType);
    const dataPoints = this.signaturePad.toData();

    let encodedImage: string|null = null;
    if (dataUrl) {
      const matches: RegExpMatchArray|null = dataUrl.match(/^data:.+\/(.+);base64,(.*)$/);
      encodedImage = matches && matches.length > 2 ? matches[2] : null;
    }
    const signatureData: ISignature = {
      pointGroups: dataPoints,
      mediaType,
      base64EncodedImage: encodedImage
    };

    if (! this.isSignatureSizeValid(signatureData)) {
        this.snackBar.open('Signature is too large, please try again', 'Dismiss', {
            duration: 8000, verticalPosition: 'top'
        });
        return;
    }

    this.session.onAction(this.screen.saveAction.action, signatureData);
  }

  protected isSignatureSizeValid(sigData: ISignature) {

    if (Configuration.maxSignaturePoints >= 0) {
        let totalSignaturePoints = 0;
        if (sigData.pointGroups) {
            sigData.pointGroups.forEach(pArray => totalSignaturePoints += pArray.length);
            this.log.info(`Total signature points: ${totalSignaturePoints}`);
        }

        if (totalSignaturePoints > Configuration.maxSignaturePoints) {
            this.log.info(`Signature point count of ${totalSignaturePoints} exceeds the Configuration.maxSignaturePoints of ` +
            `${Configuration.maxSignaturePoints}`);
            return false;
        }
    }

    if (Configuration.maxResponseSizeBytes >= 0) {
        const signatureResponseSize = JSON.stringify(sigData).length;
        this.log.info(`Signature response size: ${signatureResponseSize}`);
        if (signatureResponseSize > Configuration.maxResponseSizeBytes) {
            this.log.info(`Signature response size of ${signatureResponseSize} exceeds the Configuration.maxResponseSizeBytes of ` +
              `${Configuration.maxResponseSizeBytes}`);
            return false;
        }
    }

    return true;
  }
}

export interface ISignature {
  pointGroups: IPoint[][];
  mediaType: string;
  base64EncodedImage: string|null;
}

export interface IPoint {
  x: number;
  y: number;
  time: number;
  color?: string;
}
