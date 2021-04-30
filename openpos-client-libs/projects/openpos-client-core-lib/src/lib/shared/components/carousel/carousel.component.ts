import {
    Component,
    Input,
} from '@angular/core';

@Component({
    selector: 'app-carousel',
    templateUrl: './carousel.component.html',
    styleUrls: ['./carousel.component.scss']
})
export class CarouselComponent {
    @Input()
    get imgUrls(): string[] {
        return this._imgUrls;
    }

    set imgUrls(value: string[]) {
        if (!value) {
            value = new Array<string>();
        }

        this.displayImageUrls = this._imgUrls = value;

        this.selectImage(0);
    }

    @Input()
    altImageUrl?: string;

    @Input()
    altImageText?: string;

    displayImageUrls: string[];
    selectedImageUrl?: string;

    private _imgUrls = new Array<string>();

    selectImage(index: number) {
        if (this.displayImageUrls.length > 0) {
            index = Math.max(0, Math.min(index, this.displayImageUrls.length - 1));
            this.selectedImageUrl = this.displayImageUrls[index];
        } else {
            this.selectedImageUrl = undefined;
        }
    }

    onThumbnailError(index: number) {
        const url = this.displayImageUrls[index];

        this.displayImageUrls.splice(index, 1);

        if (this.selectedImageUrl === url) {
            if (this.displayImageUrls.length > 0) {
                this.selectedImageUrl = this.displayImageUrls[0];
            } else {
                this.selectedImageUrl = undefined;
            }
        }
    }

    onSelectedImageError() {
        this.selectedImageUrl = undefined;
    }
}
