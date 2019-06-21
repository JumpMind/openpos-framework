# Wedge Scanner Plugin

The wedge scanner acts like a keyboard and dispatches keydown events for each character in a barcode. The Wedge Scanner plugin monitors those events and when the start sequence is seen starts buffering up the events until either the stop sequence is seen or there is a 100 (ms) delay between events.

## Configuration

Since most wedge scanners are highly configurable the plugin can be configured through Client Configuration on the server with 2 different configuration messages:

### WedgeScanner

The WedgeScanner configuration object is as follows:

```yaml
    WedgeScanner:
        codeTypeLength: 1
        endSequence: 'Enter'
        startSequence: '*'
```

**codeTypeLength** is the number of characters to split off the front of the scan to represent the barcode type

**startSequence** is the key sequence that marks the start of a scan. This can be a character and optionaly a modifier key. ex 'ctrl+b' or 'alt+k'

**endSequence** is the key sequence that marks the end of a scan. Valid values are the same as startSequence.

### WedgeScannerTypes

The WedgeScannerTypes configuration maps type character recieved from the device to the correct Openpos Barcode Type.

```yaml
    WedgeScannerTypes:
        G: Code128
        B: UPCA
        C: UPCE
        E: EAN13
```
