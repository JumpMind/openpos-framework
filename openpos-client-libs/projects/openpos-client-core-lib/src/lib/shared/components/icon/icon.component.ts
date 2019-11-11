import { Component, Input, OnInit, ElementRef, Renderer2, OnChanges, SimpleChanges } from '@angular/core';
import { IconService } from '../../../core/services/icon.service';
import { Observable } from 'rxjs';
import { SafeHtml } from '@angular/platform-browser';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';

@Component({
    selector: 'app-icon',
    templateUrl: './icon.component.html',
    styleUrls: ['./icon.component.scss']
})

export class IconComponent implements OnInit, OnChanges {

    @Input()
    iconName: string;

    @Input() iconClass !: string;

    parser = new DOMParser();
    icon: Observable<SafeHtml>;

    isMobile: Observable<boolean>;

    constructor(
        private iconService: IconService, private elementRef: ElementRef,
        private renderer: Renderer2, media: OpenposMediaService) {
        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, false],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));

        this.isMobile.subscribe(mobile => {
            if (mobile) {
                this.renderer.addClass(this.elementRef.nativeElement, 'mobile');
            } else {
                this.renderer.removeClass(this.elementRef.nativeElement, 'mobile');
            }
        });
    }

    ngOnInit(): void {
        this.renderIcon(null);
    }

    ngOnChanges(changes: SimpleChanges): void {

        if (!this.iconClass) {
            this.iconClass = 'mat-24';
        } else if (this.iconClass === 'none') {
            this.iconClass = null;
        } else if (!this.iconClass.includes('mat-')) {
            this.iconClass += ' mat-24';
        }

        this.renderIcon(changes.iconClass.previousValue);
    }

    private renderIcon(previousClasses: string) {
        this.icon = this.iconService.getIconHtml(this.iconName);
        if (previousClasses) {
            previousClasses.split(' ').forEach(e => this.renderer.removeClass(this.elementRef.nativeElement, e));
        }
        if (this.iconClass) {
            this.iconClass.split(' ').forEach(e => this.renderer.addClass(this.elementRef.nativeElement, e));
        }
    }
}
