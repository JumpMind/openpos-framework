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
        const textureImageUrl = this.imageService.contentUrl('textures/grunge.png');
        const cssUrl = `url('${textureImageUrl}')`;

        this.renderer.setStyle(this.elementRef.nativeElement, '-webkit-mask-image', cssUrl);
        this.renderer.setStyle(this.elementRef.nativeElement, 'mask-image', cssUrl);
    }
}
