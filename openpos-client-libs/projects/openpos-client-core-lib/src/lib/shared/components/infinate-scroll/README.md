# Infinite Scroll

`<app-infinite-scroll>` uses the UIDataMessageService to implement 'Infinite Scroll'. When the viewable area gets close to the bottom of the loaded data it will reach out to the server for more data. You will need to implement aa UIDataMessageProvider on the server to handle the requests for more data.

Property | Description
---------|----------
`@Input() dataKey: string`  | Key to use to fetch the data from the server
`@Input() itemHeightPx: number` | How tall is the template when rendered. Needs to be fixed so we can calculate when to load more items.
`@Input() dataLoadBuffer: number` | How how many items do we want to make sure to have pre-loaded out of view. When the non-viewable items drops below the buffer we fetch more
`@Input() itemTemplate: TemplateRef<T>` | Template to apply to each item. Each item is added to the template context scope allowing you to use `let-<property>="<property>"` for any properties you want to pull out of the item. See example below.
`@Input() virtualScrollMinBufferPx: number` | How close to the edge of the rendered content do let the viewable area get before starting to render more.
`@Input() virtualScrollMaxBufferPx: number` | How far away from the edge of the viewable area do we render content.
`@Input() itemTrackByFunction: TrackByFunction<T>` | Optionally provide a track by function to improve performance of updating the list. This method should return the value to use to uniquely identify an element.
`@Input() itemClass: string` | Class to add to each item. This class will need to be global and not just scope to your component. 

Internally the component uses a `<ul>` element for the list and `<li>` elements for each item. Styling for these elements can be passed in through inputs.

### Example

```html
<app-infinite-scroll
        [dataKey]="screen.searchResultsDataKey"
        [itemTemplate]="itemTemplate"
        virtualScrollMinBufferPx="200"
        virtualScrollMaxBufferPx="400"
        [itemTrackByFunction]="resultsTrackFn"
        itemHeightPx="150"
        itemClass="item-template"
        listClass="list"
        >
    <ng-template #itemTemplate let-imageUrl="imageUrl" let-title="title" let-properties="properties">
        <img [src]="imageUrl | imageUrl">
        <div class="description">{{title}}</div>
        <div class="properties">
            <app-display-property *ngFor="let prop of properties" [property]="prop"></app-display-property>
        </div>
        <app-icon class="icon" [iconName]="screen.selectAction.icon" iconClass="md"></app-icon>
    </ng-template>
</app-infinite-scroll>
```