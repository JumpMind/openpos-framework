import { Directive, ElementRef, Renderer2, AfterViewInit } from '@angular/core';
import { Observable } from 'rxjs';
import { FloaterService } from '../../core/services/floater.service';

// tslint:disable-next-line:directive-selector
@Directive({ selector: '[fiOs]' })
export class FixediOsScrollDirective implements AfterViewInit {
    private isFloater$: Observable<boolean>;

    constructor(private el: ElementRef, floaterService: FloaterService, private renderer: Renderer2) {
        this.isFloater$ = floaterService.isFloating();
    }

    ngAfterViewInit(): void {
        this.isFloater$.subscribe((isFloating) => {
            if(isFloating){
                this.renderer.addClass(this.el.nativeElement, 'sid-nav-content-scrollblock');
            }else{
                this.renderer.removeClass(this.el.nativeElement, 'sid-nav-content-scrollblock');
            }
        });
    }
}