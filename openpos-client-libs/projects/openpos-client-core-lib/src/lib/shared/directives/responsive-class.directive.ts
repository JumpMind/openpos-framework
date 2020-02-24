import {Directive, ElementRef, OnDestroy, OnInit, Renderer2} from '@angular/core';
import {Subject} from 'rxjs';
import {OpenposMediaService} from '../../core/media/openpos-media.service';
import {takeUntil, tap} from "rxjs/operators";

@Directive({
    selector: '[responsive-class]'
})
export class ResponsiveClassDirective implements OnInit, OnDestroy {
    destroyed = new Subject();
    breakpointNameToClasses = new Map<string, string[]>();

    constructor(
        private mediaService: OpenposMediaService,
        private renderer: Renderer2,
        private elementRef: ElementRef
    ) {
    }

    updateClassList(): void {
        const breakpointNames = Array.from(this.mediaService.breakpointToName.values());

        breakpointNames.forEach(breakpointName => {
            const classNames = this.getClassNames(breakpointName);
            this.breakpointNameToClasses.set(breakpointName, classNames);
        });
    }

    getClassNames(breakpointName: string): string[] {
        const deviceTypeOrientationSeparatorIndex = breakpointName.lastIndexOf('-');
        let classNames = [breakpointName];

        if (deviceTypeOrientationSeparatorIndex >= 0) {
            const deviceType = breakpointName.slice(0, deviceTypeOrientationSeparatorIndex);
            const orientation = breakpointName.slice(deviceTypeOrientationSeparatorIndex + 1);
            classNames = [deviceType, `${deviceType}-${orientation}`];
        }

        return classNames;
    }

    removeClasses(): void {
        const classNameLists = Array.from(this.breakpointNameToClasses.values());
        // Flatten the arrays
        const classNames = [].concat(...classNameLists);

        classNames.forEach(className => this.renderer.removeClass(this.elementRef.nativeElement, className));
    }

    addClasses(classNames: string[]): void {
        if (!classNames) {
            return;
        }

        classNames.forEach(className => this.renderer.addClass(this.elementRef.nativeElement, className));
    }

    ngOnInit(): void {
        this.updateClassList();

        this.mediaService.observe(this.breakpointNameToClasses).pipe(
            tap(() => this.removeClasses()),
            tap(classNames => this.addClasses(classNames)),
            takeUntil(this.destroyed)
        ).subscribe();
    }

    ngOnDestroy(): void {
        this.destroyed.next();
    }
}
