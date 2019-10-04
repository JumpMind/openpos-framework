import {Directive, ElementRef, Inject, OnDestroy, Renderer2} from '@angular/core';
import {Subscription} from 'rxjs';
import {OpenposMediaService} from '../../core/services/openpos-media.service';
import {DEFAULT_RESPONSIVE_MAP} from '../../core/default-responsive-map';

@Directive({
  selector: '[responsive-class]'
})
export class ResponsiveClassDirective implements OnDestroy{

    readonly subscription: Subscription;

    constructor(
        @Inject(DEFAULT_RESPONSIVE_MAP)
      private breakpointToClassName: Map<string,string>,
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
