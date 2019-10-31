import {Component, Input } from '@angular/core';
import {ImageService} from '../../../core/services/image.service';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss']
})
export class ImageComponent {

  @Input()
  imageUrl: string;

  @Input()
  altImageUrl: string;

  @Input()
  altText: string;

  constructor( private imageService: ImageService) { }

  imageError(image){
    let altImage = this.imageService.replaceImageUrl(this.altImageUrl);
    if( image.src !== altImage ){
      image.src = altImage;
    }
  }

}
