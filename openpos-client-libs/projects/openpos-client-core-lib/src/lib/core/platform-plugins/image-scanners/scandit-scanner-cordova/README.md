# Scandit Cordova Scanner Plugin

This plugin supports integration of the [Scandit Cordova plugin](https://docs.scandit.com/data-capture-sdk/cordova/add-sdk.html)

## Configuration

There are many configuration options and all can be set through Client Configuration on the server:

```yaml
  ScanditCordova:
    licenseKey: 'YOUR-KEY'
    enabledCodes: 'AZTEK, CODABAR, CODE11, CODE25_NI2OF5, CODE39, CODE93, CODE128, EAN8, GS1DATABAR, MAXICODE, MSI_PLESSEY, PDF417, QRCODE, RM4SCC, UPCA, UPCE'
    codeDuplicateFilter: Time in ms or -1 for no duplicates
    viewFinderType: Laser | Rectangular | Spotlight | None
    enableBeep: true | false
    enableVibrate: true | false
``` 