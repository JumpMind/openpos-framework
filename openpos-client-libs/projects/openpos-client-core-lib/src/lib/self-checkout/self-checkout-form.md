# Self Checkout Form

The self checkout form screen is a basic form screen that also supports Google autocomplete for addresses. This screen utilizes the same form components as the core form screens.

![Self Checkout Loyalty Form](assets/self-checkout/self-checkout-loyalty-form.png)

## Setting the Image

The form screen logo is set via the `imageUrl` property on the UIMessage. This can be set from the server, for example:

``` java
screen.setImageUrl("content:logo");
```

## Form Inputs

The self checkout prompt screen uses the shared `DynamicFormPartComponent` and `AutoCompleteAddressPartComponent` for the form inputs. Styling for these components can be overridden by modifying the scss in a theme. These components can also be completely overriden with new components by replacing the prompt `DynamicFormPartComponent` and `AutoCompleteAddressPartComponent` with a new screen part.
