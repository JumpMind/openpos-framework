# Button Action Timer

If included as a child element to a button that has an associated `IActionItem`, a timer will be started when the button is first displayed.  In order for the timer to be enabled, the given `IActionItem` must minimally contain an `IActionTimer` (via the `actionTimer` attribute) with a `timeoutSecs` value greater than 0. 

After `actionTimer.timeoutSecs` have elapsed, the given `IActionItem` will be sent back to the server.  There are also options to provide message text before and after the optional countdown display which are described below.

See the docs for `IActionTimer` for additional explanation on the settings available on the timer and the timer countdown text displayed.

## Button Action Timer Properties

| Property| Desription
|---------|-----------
| `@Input() action: IActionItem` | The action to send back to the server after `action.actionTimer.timeoutSecs` has elapsed.  If `action.actionTimer` is null or the `action.actionTimer.timeoutSecs` is <= 0, no timer will be set.
| `@Input() reset: Observable<boolean>` | An optional boolean Observable that will cause the timer to be reset back to its initial state if a value of `true` is emitted from the Observable.  An example of where this might be used is on screens where the content is refreshed asynchronously from the server.

## Example usage
```
<app-primary-button ... [actionItem]="primaryButton">
    {{primaryButton.title}}
    <app-button-action-timer [action]="primaryButton" class="primary-inverse"></app-button-action-timer>
</app-primary-button>
```

In the example above, the `primaryButton` variable is of type `IActionItem`.  If the `primaryButton` object has an `actionTimer` object set on it with a `actionTimer.timeoutSecs` > 0, the timer will be enabled.