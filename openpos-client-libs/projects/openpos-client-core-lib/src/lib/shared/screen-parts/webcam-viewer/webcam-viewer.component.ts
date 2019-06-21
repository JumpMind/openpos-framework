import { Component, ViewChild, AfterViewInit, ElementRef, OnDestroy } from '@angular/core';
import { ScreenPartComponent } from '../screen-part';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { MessageProvider } from '../../providers/message.provider';
import { WebcamViewerInterface } from './webcam-viewer.interface';

@ScreenPart({
    name: 'WebcamViewerPart'
})
@Component({
    selector: 'app-webcam-viewer',
    templateUrl: './webcam-viewer.component.html',
    styleUrls: ['./webcam-viewer.component.scss']
})
export class WebcamViewerComponent extends ScreenPartComponent<WebcamViewerInterface> implements AfterViewInit, OnDestroy {

    @ViewChild('videoElement') video: ElementRef;
    private mediaStream: MediaStream;

    constructor(messageProvider: MessageProvider) {
        super(messageProvider);
    }

    screenDataUpdated() {
    }

    buildScreen() { }


    ngAfterViewInit() {
        const videoConfig = { video: true };

        if (this.video && navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia(videoConfig)
                .then(stream => {
                    this.mediaStream = stream;
                    this.video.nativeElement.srcObject = this.mediaStream;
                    this.video.nativeElement.play();
                });
        }

    }

    ngOnDestroy() {
        if (this.video && this.mediaStream && this.mediaStream.getTracks() && this.mediaStream.getTracks().length > 0) {
            this.mediaStream.getTracks()[0].stop();
        }
    }

}
