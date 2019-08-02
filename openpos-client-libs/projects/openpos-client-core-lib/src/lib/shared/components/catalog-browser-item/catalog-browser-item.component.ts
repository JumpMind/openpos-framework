import { Component, Input, OnInit, Inject, forwardRef } from '@angular/core';
import { Subject } from 'rxjs';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { Logger } from '../../../core/services/logger.service';
import { PersonalizationService } from '../../../core/personalization/personalization.service';
import { IUrlMenuItem } from '../../../core/interfaces/url-menu-item.interface';

@Component({
    selector: 'app-catalog-browser-item',
    templateUrl: './catalog-browser-item.component.html',
    styleUrls: ['./catalog-browser-item.component.scss']
})
export class CatalogBrowserItemComponent implements OnInit {
    @Input() item: ISellItem;
    @Input() loadFailImgSrc: string;

    loadingError = false;
    private imageLoaded$ = new Subject<boolean>();
    private imageError$ = new Subject<boolean>();

    imageSrc: string;
    imageLoaded = true;
    private _loadFailImgSrcUrl: string;

    // @ViewChild('itemImg') itemImage: Element;

    constructor(private log: Logger,
        @Inject(forwardRef(() => PersonalizationService))private personalization: PersonalizationService) {
    }

    ngOnInit(): void {
        let imageTimer: any = null;

        if (this.loadFailImgSrc) {
            this._loadFailImgSrcUrl = this.loadFailImgSrc;
            if (! this.loadFailImgSrc.startsWith('http')) {
                this._loadFailImgSrcUrl = `${this.personalization.getServerBaseURL()}/${this.loadFailImgSrc}`;
            }
        }

        if (this.showImage()) {
            this.imageSrc = (<IUrlMenuItem>this.item.menuItems[0]).url;
            imageTimer = setTimeout( () => {
                this.imageSrc = this._loadFailImgSrcUrl;
                this.imageLoaded = false;
                },
                2000
            );

            this.imageLoaded$.subscribe(loaded => {
                this.imageLoaded = loaded;
                if (imageTimer) {
                    clearTimeout(imageTimer);
                }
            });
        }
    }

    showImage(): boolean {
        if (this.item.menuItems[0] && ! this.item.menuItems[0].hasOwnProperty('url')) {
            return false;
        }

        if (! (<IUrlMenuItem>this.item.menuItems[0]).url ) {
            return this.item.description === null;
        }
        return true;
    }

    onImageLoaded(): void {
        this.imageLoaded$.next(true);
    }

    onImageLoadError(): void {
        this.imageSrc = this._loadFailImgSrcUrl;
        this.imageLoaded$.next(false);
        this.imageError$.next(true);
    }
}
