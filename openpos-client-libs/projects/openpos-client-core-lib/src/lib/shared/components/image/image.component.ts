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

  /**
   * Alternate image boolean that shows if alternate image loading failed
   */
  altImageNotLoading : boolean = false;

  /**
   * Boolean that shows if img src and altImg src failed
   */
  noImagesFound: boolean = false;

  constructor( private imageService: ImageService) { }

  imageError(image){
    // throw error in case original src, alt img src and img not found src are failing
    if(this.noImagesFound){
      throw Error('Image is not loading')
    }

    // if there is no altImage Url or altImageUrl failed set altImageURL to '' so it will trigger image-not-found from
    // config in imageService
    if(!this.altImageUrl || this.altImageNotLoading ){
      this.noImagesFound = true;
      this.altImageUrl = ''
    }

    let altImage = this.imageService.replaceImageUrl(this.altImageUrl);
    if( image.src !== altImage ){
      image.src = altImage;
    }
    this.altImageNotLoading = true;
  }

}
