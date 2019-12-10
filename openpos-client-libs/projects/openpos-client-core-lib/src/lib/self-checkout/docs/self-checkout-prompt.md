# Self Checkout Prompt

The self checkout prompt screen serves as a basic, single input screen that can be used for things such as email entry, phone entry, etc. This component again shares the same prompt input as the core prompt screen.

![Self Checkout Search](images/self-checkout-loyalty-search.png)

## Setting the Image

The prompt screen logo is set via the `imageUrl` property on the UIMessage. This can be set from the server, for example:

``` java
screen.setImageUrl("content:logo");
```

## Prompt Input

The self checkout prompt screen uses the shared `PromptFormPartComponent` for the prompt input. Styling for this component can be overriden by modifying the scss in a theme. This component can also be completely overriden with a new component by replacing the prompt `PromptFormPartComponent` with a new screen part.
