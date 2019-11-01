# Carousel

`<app-carousel>` takes a list of template references with the id `carouselItem`. These references are typically images, however they do not need to be. If there is more than one template reference in the list, the carousel will show navigation dots and the user is able to arrow to the right and left to show the items in the carousel.

Property | Description
---------|------------
`@Input() carouselSize = 'lg';` | Key to specify the size of the carousel. As default, the size is set to large ('lg'). Other options include 'sm' and 'md'.
`@Input() carouselItemClass: string` | An optional css class can be passed in that will be applied to the display of items. For example, with images, a class to round the corners of the images can be passed in.

## Example Use

```html
<app-carousel [carouselItemClass]="'rounded-edge'">
    <ng-template *ngFor="let image of screen.imageUrls" #carouselItem>
        <img  [src]="image | imageUrl">
    </ng-template>
</app-carousel>
```