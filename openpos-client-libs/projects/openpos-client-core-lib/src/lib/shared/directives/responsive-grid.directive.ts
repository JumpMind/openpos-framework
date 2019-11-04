import {Directive, ElementRef, Inject, InjectionToken, Input, OnDestroy, OnInit, Renderer2} from '@angular/core';
import {Subscription} from 'rxjs';
import {OpenposMediaService} from '../../core/media/openpos-media.service';

@Directive({
    selector: '[opAreas], [opAreas.mobile], [opAreas.tablet], [opAreas.desktop]'
})
export class ResponsiveGridDirective implements OnInit, OnDestroy {
    private theMap: Map<string, string> = new Map;

    private breakpointToClassName = new Map([['xs', 'mobile'],['sm', 'mobile'],['md', 'tablet'],['lg', 'desktop'],['xl', 'desktop']]);

    @Input('opAreas')
    set areas( value: string){
        this.theMap.set('', value);
    }

    @Input('opAreas.mobile')
    set mobileAreas( value: string){
        this.theMap.set('mobile', value);
    };

    @Input('opAreas.tablet')
    set tabletAreas( value: string){
        this.theMap.set('tablet', value);
    };

    @Input('opAreas.desktop')
    set desktopAreas( value: string){
        this.theMap.set('desktop', value);
    }

    private subscription: Subscription;

    constructor(private mediaService: OpenposMediaService,
                 private el: ElementRef,
                 private renderer: Renderer2){
    }

    ngOnDestroy(): void {
        if(this.subscription){
            this.subscription.unsubscribe();
        }
    }

    private getAreas(input: string) {
        return (input || 'none').split('|').map(v => `"${v.trim()}"`).join(' ');
    }

    ngOnInit(): void {
        this.subscription = this.mediaService.mediaObservableFromMap(this.breakpointToClassName).subscribe( className => {
            this.renderer.setStyle(this.el.nativeElement, 'display', 'grid');
            let areas = this.getAreas(this.theMap.get(''));
            if( this.theMap.has(className)){
                areas = this.getAreas(this.theMap.get(className));
            }
            this.renderer.setStyle(this.el.nativeElement, 'grid-template-areas', areas);
        });
    }
}