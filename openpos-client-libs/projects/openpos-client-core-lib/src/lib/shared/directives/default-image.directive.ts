import { Directive, HostListener, Input, HostBinding, Inject, forwardRef } from '@angular/core';
import { DiscoveryService } from '../../core/discovery/discovery.service';


@Directive({
    // tslint:disable-next-line:directive-selector
    selector: 'img[default]'
})
export class DefaultImageDirective {
    @HostBinding('src') src: string;
    @Input() default: string;

    constructor(@Inject(forwardRef(() => DiscoveryService))private discovery: DiscoveryService) {

    }
    @HostListener('error')
    onError() {
        this.src = this.makeUrl(this.default);
    }

    private makeUrl(imgPath: string): string {
        return `${this.discovery.getServerBaseURL()}/img/${imgPath}`;
    }

}
