import {Component, ElementRef, OnInit, Renderer2} from '@angular/core';
import {ImageService} from '../../../core/services/image.service';

@Component({
    selector: 'app-stamp',
    templateUrl: './stamp.component.html',
    styleUrls: ['./stamp.component.scss']
})
export class StampComponent implements OnInit {
    constructor(private imageService: ImageService, private renderer: Renderer2, private elementRef: ElementRef) {
    }

    ngOnInit(): void {
        this.updateStyles();
    }

    updateStyles(): void {
        const textureImageUrl = this.getTextureUrl();
        const cssUrl = `url('${textureImageUrl}')`;

        this.renderer.setStyle(this.elementRef.nativeElement, '-webkit-mask-image', cssUrl);
        this.renderer.setStyle(this.elementRef.nativeElement, 'mask-image', cssUrl);
    }

    getTextureUrl(): string {
        // Textures are not expected to change right now, so it's ok to build the content URL on the client in this case
        const url = this.imageService.replaceImageUrl('${apiServerBaseUrl}/appId/${appId}/deviceId/${deviceId}/content?contentPath=');
        return `${url}textures/grunge.png&provider=classPathContentProvider`;
    }
}
