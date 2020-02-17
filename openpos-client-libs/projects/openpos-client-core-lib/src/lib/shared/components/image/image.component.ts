import {Component, Input } from '@angular/core';
import {ImageService} from '../../../core/services/image.service';

/**
 * A wrapper around an `<img>` element that will run the URLs through the image service and swap in an alternate image if the primary results in an error.
 */
@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss']
})
export class ImageComponent {

  /**
   * URL for the primary image
   */
  @Input()
  imageUrl: string;

  /**
   * URL for the alternate image
   */
  @Input()
  altImageUrl: string;

  /**
   * Alternate text if neither image works
   */
  @Input()
  altText: string;

  constructor( private imageService: ImageService) { }

  imageError(image){
    if(!this.altImageUrl){
      throw Error('Alt image not defined')
    }
    let altImage = this.imageService.replaceImageUrl(this.altImageUrl);
    if( image.src !== altImage ){
      image.src = altImage;
    }
  }

}
