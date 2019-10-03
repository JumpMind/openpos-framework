import {Directive, ElementRef, OnDestroy, Renderer2} from '@angular/core';
import {Subscription} from 'rxjs';
import {OpenposMediaService} from '../../core/services/openpos-media.service';

@Directive({
  selector: '[responsive-class]'
})
export class ResponsiveClassDirective implements OnDestroy{

    private breakpointToClassName = new Map([
        ['xs', 'mobile'],
        ['sm', 'mobile'],
        ['md', 'desktop'],
        ['lg', 'desktop'],
        ['xl', 'desktop']
    ]);

    readonly subscription: Subscription;

    constructor(
      mediaService: OpenposMediaService,
      renderer: Renderer2,
      elRef: ElementRef) {
      this.subscription = mediaService.mediaObservableFromMap(this.breakpointToClassName).subscribe( className => {
          this.breakpointToClassName.forEach( c => {
              renderer.removeClass(elRef.nativeElement, c);
          });
          renderer.addClass(elRef.nativeElement, className);
      });
    }

    ngOnDestroy(): void {
      if(this.subscription){
          this.subscription.unsubscribe();
      }
    }

}
