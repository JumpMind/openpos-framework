import { Component, Input, OnInit, ElementRef, Renderer2, OnChanges } from '@angular/core';
import {MediaService} from '@angular/flex-layout';
import { IconService } from '../../../core/services/icon.service';
import { Observable } from 'rxjs';
import { SafeHtml } from '@angular/platform-browser';
import {OpenposMediaService} from '../../../core/services/openpos-media.service';

@Component({
    selector: 'app-icon',
    templateUrl: './icon.component.html',
    styleUrls: ['./icon.component.scss']
})

export class IconComponent implements OnInit, OnChanges {

    @Input()
    iconName: string;

    private lastIcon: string;
    @Input() iconClass;

    parser = new DOMParser();
    icon: Observable<SafeHtml>;

    constructor(private iconService: IconService, private elementRef: ElementRef, private renderer: Renderer2, private media: MediaService) {
        media.subscribe( () => this.renderIcon() );
    }

    ngOnInit(): void {
        this.renderIcon();
    }

    ngOnChanges(): void {

        if (!this.iconClass) {
            this.iconClass = 'mat-24';
        } else if(this.iconClass === 'none') {
            this.iconClass = null;
        } else if (!this.iconClass.includes('mat-')) {
            this.iconClass += ' mat-24';
        }

        this.renderIcon();
    }

    private renderIcon() {
        this.icon = this.iconService.getIconHtml(this.iconName);
        if (this.iconClass) {
            this.iconClass.split(' ').forEach(e => this.renderer.addClass(this.elementRef.nativeElement, e));
            if( this.media.isActive('lt-md')) {
                this.renderer.addClass(this.elementRef.nativeElement, 'mobile')
            }

        }
    }
}
