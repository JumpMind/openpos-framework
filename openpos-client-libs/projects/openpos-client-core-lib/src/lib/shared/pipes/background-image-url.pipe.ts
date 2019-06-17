import { Pipe, PipeTransform } from '@angular/core';
import { ImageService } from '../../core/services/image.service';

@Pipe({ name: 'backgroundImageUrl' })
export class BackgroundImageUrlPipe implements PipeTransform {

    constructor(private imageService: ImageService) { }

    transform(url: string) {
        let style = {};
        if (url) {
            url = this.imageService.replaceImageUrl(url);
            style = {
                'background-image': `url(${url})`,
                'background-repeat': 'no-repeat',
                'background-size': 'cover',
                'background-position': 'center'
            };
        }
        return style;
    }

}
