# Actions

Actions represent something that happens on the client that needs to be reported to the server. The most common action is a button clicked by the user. The button would have an [IActionItem](action-item.interface.ts) associated with it and when clicked `doAction` should be called passing in the ActionItem and optionally any payload that needs to be sent back to the server.

## Action Confirmation

`ActionItem` has a property `confirmationDialog` of type [IConfirmationDialog](confirmation-dialog.interface.ts) that when set will prompt the user to confirm the action before sending it to the server. The `IConfirmationDialog` object allows for customizing the message presented to the user as well as the titles of the positive and negative buttons.

## Action Payload

Sometimes the button that performs the action is on a different component than the payload that needs to be sent with the action. The problem is the 2 components don't know about each other and we don't want them to so they can remain loosely coupled. This is where `registerActionPayload` is helpful. This allows you to pass in a fat arrow function to run before the action is packaged up and sent to the server.

```typescript
actionService.registerActionPayload( 'actionName', () => this.payload );
```

_**Note** only one playload can be registered with an action so last one to register wins_

## Action Disablers

Sometimes the logic for disabling a UIElement associated with an ActionItem is on a seperate component than the UIElement. Instead of coupling the 2 component we can use an ActionDisabler. An ActionDisabler is an Observable that represents the current state of the action. Create an ActionDisabler by calling `registerActionDisabler` passing in the name of the action to control and a boolean Observable that will take updates to the disabled state. `registerActionDisabler` returns a subscription to be used to clean up when your component is destroyed.

```typescript
actionDisabled$ = new BehaviorSubject<boolean>(false);

...

actionService.registerActionPayload( 'actionName', this.actionDisabled$ );
```

Then monitor the logic that should update the disabled state

```typescript
onValueChanged() {
    this.actionDisabled$.next(this.formValid);
}
```

On the other end you can either actively check the disabled state with `actionService.actionIsDisabled(actionName)` or subscribe or bind to the `actionService.actionIsDisabled$(actionName)` observable.

```html
<button [disabled]="actionService.actionIsDisabled$(actionName) | async" >
```

Multiple ActionDisablers are allowed, the observables are merged and last value streamed in wins.
