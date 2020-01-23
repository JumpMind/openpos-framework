# Dialog Timer Part

If included as a message part on a dialog, a timer will be started when the dialog is first displayed.  The timer period is controlled by `timeoutSecs`.
After `timeoutSecs` have elapsed, the dialog will be automatically closed and, if a `timeoutAction` is supplied, that action will be sent back to the server as well.  There are also options to provide message text before and after the optional countdown display which are described below.

## Dialog Message Part Properties

| Property | Type   | Desription
|----------|--------|------------
| timeoutAction | string | The action to send back to the server after `timeoutSecs` has elapsed.  If not provided, then the dialog will just be closed after `timeoutSecs` have elapsed.
| timeoutSecs | number | The number of seconds that will elapse before the optional `timeoutAction` is triggered and the dialog is closed.
| timeoutMessage | string | Optional text to display at the front of the timer countdown. Spacing within the text is preserved.
| displayCountdown | boolean | Whether or not a countdown timer should be displayed.  If displayed, it will be displayed after the optional `timeoutMessage` and before the optional `countdownUnitsText`. Default value is `true`.
| countdownUpdateFrequencyMillis  | number | The number of milliseconds that will be allowed to elapse between updates to the displayed countdown. Default value is 1000ms.
| countdownUnitsText | string | An optional text string to display after the displayed countdown.  Default value is 's'. Spacing is preserved.
