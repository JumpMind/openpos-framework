import { Directive, Renderer2, ElementRef, Input, OnInit, OnDestroy } from '@angular/core';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { Subscription } from 'rxjs';

const selectors = `
[klass], [klass.xs], [klass.sm], [klass.md], [klass.lg], [klass.xl],
[klass.lt-sm], [klass.lt-md], [klass.lt-lg], [klass.lt-xl],
[klass.gt-xs], [klass.gt-sm], [klass.gt-md], [klass.gt-lg]
`;

  @Directive({selector: selectors})
  export class KlassDirective implements OnInit, OnDestroy  {
    private prevClass: string;
    private inputMap: Map<string, string> = new Map;
    private subscriptions = new Subscription();

    @Input('klass')
    set klass(val: string) {
        this.inputMap.set('', val);
    }
    @Input('klass.xs')
    set klassXs(val: string) {
        this.inputMap.set('xs', val);
    }
    @Input('klass.sm')
    set klassSm(val: string) {
        this.inputMap.set('sm', val);
    }
    @Input('klass.md')
    set klassMd(val: string) {
        this.inputMap.set('md', val);
    }
    @Input('klass.lg')
    set klassLg(val: string) {
        this.inputMap.set('lg', val);
    }
    @Input('klass.xl')
    set klassXl(val: string) {
        this.inputMap.set('xl', val);
    }
    @Input('klass.lt-sm')
    set klassLtSm(val: string) {
        this.inputMap.set('xs', val);
    }
    @Input('klass.lt-md')
    set klassLtMd(val: string) {
        this.inputMap.set('xs', val);
        this.inputMap.set('sm', val);
    }
    @Input('klass.lt-lg')
    set klassLtLg(val: string) {
        this.inputMap.set('xs', val);
        this.inputMap.set('sm', val);
        this.inputMap.set('md', val);
    }
    @Input('klass.lt-xl')
    set klassLtXl(val: string) {
        this.inputMap.set('xs', val);
        this.inputMap.set('sm', val);
        this.inputMap.set('md', val);
        this.inputMap.set('lg', val);
    }
    @Input('klass.gt-xs')
    set klassGtXs(val: string) {
        this.inputMap.set('sm', val);
        this.inputMap.set('md', val);
        this.inputMap.set('lg', val);
        this.inputMap.set('xl', val);
    }
    @Input('klass.gt-sm')
    set klassGtSm(val: string) {
        this.inputMap.set('md', val);
        this.inputMap.set('lg', val);
        this.inputMap.set('xl', val);
    }
    @Input('klass.gt-md')
    set klassGtMd(val: string) {
        this.inputMap.set('lg', val);
        this.inputMap.set('xl', val);
    }
    @Input('klass.gt-lg')
    set klassGtLg(val: string) {
        this.inputMap.set('xl', val);
    }

    constructor(private mediaService: OpenposMediaService, private renderer: Renderer2, private el: ElementRef) {
    }

    ngOnInit(): void {
        this.subscriptions.add(this.mediaService.mediaObservableFromMap(this.inputMap).subscribe( c => {

            // Remove old class first incase there is overlap
            if ( this.prevClass ) {
                this.prevClass.split(' ').forEach( klass => {
                    this.renderer.removeClass( this.el.nativeElement, klass);
                });
            }
            if ( c ) {
                c.split(' ').forEach( klass => {
                    this.renderer.addClass(this.el.nativeElement, klass);
                });
            }
            this.prevClass = c;
        }));
      }

      ngOnDestroy(): void {
        if (!!this.subscriptions) {
            this.subscriptions.unsubscribe();
        }
      }
  }
