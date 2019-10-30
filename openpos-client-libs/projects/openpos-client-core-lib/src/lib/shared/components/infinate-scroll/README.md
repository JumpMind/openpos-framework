# Infinite Scroll

`<app-infinite-scroll>` uses the UIDataMessageService to implement 'Infinite Scroll'. When the viewable area gets close to the bottom of the loaded data it will reach out to the server for more data. You will need to implement aa UIDataMessageProvider on the server to handle the requests for more data.

Property | Description
---------|----------
`@Input() dataKey: string`  | Key to use to fetch the data from the server
`@Input() itemHeightPx: number` | How tall is the template when rendered. Needs to be fixed so we can calculate when to load more items.
`@Input() dataLoadBuffer: number` | How close to the bottom of the current dataset do we get before loading more.
`@Input() itemTemplate: TemplateRef<T>` | Template to apply to each item
`@Input() virtualScrollMinBufferPx: number` | How close to the edge of the rendered content do let the viewable area get before starting to render more.
`@Input() virtualScrollMaxBufferPx: number` | How far away from the edge of the viewable area do we render content.
`@Input() itemTrackByFunction: TrackByFunction<T>` | Optionally provide a track by function to improve performance of updating the list. This method should return the value to use to uniquely identify an element.


## Example Use

```html

```