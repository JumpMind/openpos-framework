import {Component, Input } from '@angular/core';
import {ImageService} from '../../../core/services/image.service';
import {ImageSource} from "./image-source.enum";

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
   * Original image source
   */
  orgImgSrc: string;

  /**
   * Array of image source options
   */
  imageSources = [ImageSource.ALT_IMG, ImageSource.DEFAULT_NOT_FOUND_IMG, ImageSource.IMG_FAILED]

  constructor( private imageService: ImageService) { }

  imageError(image){
    let imgSource = this.imageSources[0];
    let imgUrl;
    switch (imgSource) {
      case ImageSource.ALT_IMG:
        this.orgImgSrc = image.src;
        imgUrl = this.imageService.replaceImageUrl(this.altImageUrl);
        break;
      case ImageSource.DEFAULT_NOT_FOUND_IMG:
        imgUrl = this.imageService.replaceImageUrl('');
        break;
      case ImageSource.IMG_FAILED:
          if(this.altImageUrl){
            throw Error(`Images ${this.orgImgSrc} && ${this.altImageUrl} are not loading`)
          }
          throw Error(`Image ${this.orgImgSrc} is not loading`)
    }

    image.src = imgUrl;
    this.imageSources.shift();
  }

}
