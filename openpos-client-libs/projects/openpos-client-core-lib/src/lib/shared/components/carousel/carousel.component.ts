import { Component, ContentChildren, QueryList, TemplateRef, AfterContentInit, Input } from '@angular/core';
import { trigger, style, animate, transition, query, group} from '@angular/animations';

@Component({
    selector: 'app-carousel',
    templateUrl: './carousel.component.html',
    styleUrls: ['./carousel.component.scss'],
    animations: [
        trigger('moveInOut', [
            transition(':increment', group([
                style({
                    position: 'relative'
                }),
                query(':enter, :leave', [
                    style({
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%'
                    })
                ]),
                query(':enter', [
                  style({ left: '-100%'})
                ]),
                group([
                  query(':leave', [
                    animate('800ms ease-out', style({ left: '100%'}))
                  ]),
                  query(':enter', [
                    animate('800ms ease-out', style({ left: '0%'}))
                  ])
                ]),
            ])),
            transition(':decrement', group([
                style({
                    position: 'relative'
                }),
                query(':enter, :leave', [
                    style({
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%'
                    })
                ]),
                query(':enter', [
                    style({ left: '100%'})
                ]),
                query(':enter', [
                  animate('800ms ease-out', style({ left: '0%'}))
                ]),
                query(':leave', [
                  animate('800ms ease-out', style({ left: '-100%' }))
                ])
            ]))
        ])
    ]
})
export class CarouselComponent implements AfterContentInit {
    @Input() carouselSize = 'lg';
    @Input() carouselItemClass: string;

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
