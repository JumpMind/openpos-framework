import { Component, ContentChildren, QueryList, TemplateRef, AfterContentInit } from '@angular/core';

@Component({
    selector: 'app-carousel',
    templateUrl: './carousel.component.html',
    styleUrls: ['./carousel.component.scss']
})
export class CarouselComponent implements AfterContentInit {

    @ContentChildren('carouselItem') items: QueryList<TemplateRef<any>>;
    currentItem: TemplateRef<any>;
    index = 0;

    ngAfterContentInit(): void {
        this.currentItem = this.items.toArray()[this.index];
    }

    moveForward(): void {
        this.index++;
        this.currentItem = this.items.toArray()[this.index];
    }

    moveBackward(): void {
        this.index--;
        this.currentItem = this.items.toArray()[this.index];
    }

    forwardEnabled(): boolean {
        return this.index < this.items.length - 1;
    }

    backwardEnabled(): boolean {
        return this.index > 0;
    }

    isDotActive(dotIndex: number): boolean {
        return dotIndex === this.index;
    }
}
