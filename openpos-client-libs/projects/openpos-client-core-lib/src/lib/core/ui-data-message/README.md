# UI Data Message

Sending large lists of data to the client could cause slow rendering of the screen or exceed message size limits. UI Data Message allows you to give the core a `UIDataMessageProvider` and a key to allow the client to fetch the data from the server in chunks instead of holding up the rendering of the screen.

To add a data provider to your screen provide a map of providers and keys to the `showScreen` method and send the key to the client in place of the data of the screen.

```java
ItemSearchResults screen = ItemSearchResults.builder()
        .searchResultsDataKey("SearchResultsData")
        .build();

stateManager.showScreen(screen, new HashMap<String, UIDataMessageProvider>(){{put("SearchResultsData", itemInquiryResultsUIMessageDataProvider);}});
```

Your provider is some object that knows how to get the data for this screen and extends `UIDataMessageProvider`.

```java
public class ItemInquiryResultsUIMessageDataProvider extends UIDataMessageProvider<ItemSearchResult> {

    public void search( String searchCriteria ){
        //We are doing a new search so set the newSeries flag to tell the core to throw away the old cache
        setNewSeries(true);
    }

    @Override
    public List<ItemSearchResult> getNextDataChunk() {
            //Logic to get next set of data
    }

    @Override
    public void reset() {
        //How do we reset back to the first data chunk. 
    }
}
```

Then on the client use the UIDataMessageService to get and Observable for your data.

```typescript
results$: Observable<ItemSearchResult[]>;

constructor( injector: Injector, private resultsService: UIDataMessageService){
    super(injector);
}

buildScreen() {
    this.results$ = this.resultsService.getData$(this.screen.searchResultsDataKey)
}
```

When you are ready to pull the next chunk from the server call `requestMoreData` on `UIDataMessageService` and the new results will be accumulated with the rest of the data and pushed out to your observable.

```typescript
this.resultsService.requestMoreData(this.screen.searchResultsDataKey);
```

Example template usage.

```html
<ul>
    <li *ngFor="let result of results$ | async; trackBy: resultsTrackFn">
        ...
    </li>
</ul>
```