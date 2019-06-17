import { Pipe, PipeTransform } from '@angular/core';
import { ImageService } from '../../core/services/image.service';

@Pipe({ name: 'imageUrl' })
export class ImageUrlPipe implements PipeTransform {

    constructor(private imageService: ImageService) { }

    transform(url: string) {
        return this.imageService.replaceImageUrl(url);
    }

}
