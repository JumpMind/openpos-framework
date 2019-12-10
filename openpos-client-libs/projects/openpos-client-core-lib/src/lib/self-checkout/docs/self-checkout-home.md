# Self Checkout Home

The self checkout home screen is a basic splash screen that the user taps to start the self checkout flow. The splash screen image can be set dynamically, as well as the text and logos.

![Self Checkout Home](images/self-checkout-home.png)

## Setting the Splash Image

The splash screen image is set via the `backgroundImage` property on the UIMessage. This can be set from the server, for example:

``` java
screen.setBackgroundImage("content:home");
```

## Setting the Logo

The home screen logo is set via the `imageUrl` property on the UIMessage. This can be set from the server, for example:

``` java
screen.setImageUrl("content:logo");
```

## Setting the Home Text

The home screen text is set via the `prompt` property on the UIMessage. This can be set from the server, for example:

``` java
screen.setPrompt("key:selfcheckout:home.prompt");
```
