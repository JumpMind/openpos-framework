import { Directive, HostListener, Input, HostBinding, Inject, forwardRef } from '@angular/core';
import { PersonalizationService } from '../../core/services/personalization.service';


@Directive({
    // tslint:disable-next-line:directive-selector
    selector: 'img[default]'
})
export class DefaultImageDirective {
    @HostBinding('src') src: string;
    @Input() default: string;

    constructor(@Inject(forwardRef(() => PersonalizationService))private personalization: PersonalizationService) {

    }
    @HostListener('error')
    onError() {
        this.src = this.makeUrl(this.default);
    }

    private makeUrl(imgPath: string): string {
        return `${this.personalization.getServerBaseURL()}/img/${imgPath}`;
    }

}
