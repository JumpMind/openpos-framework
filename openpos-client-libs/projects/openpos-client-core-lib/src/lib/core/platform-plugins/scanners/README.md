# Scanner Framework Overview

The scanner framework provides a generic mechanism for interacting with different types of scanners.

Each needs to implement [IScanner](scanner.interface.ts), be made `@Injectable` and provided using the `SCANNERS` injection token.

```typescript
{ provide: SCANNERS, useExisting: WedgeScannerPlugin, multi: true },
```

Then anyone who wants access to a scan should use [ScannerService](scanner.service.ts), and call `startScanning()` which returns an Observable of [IScanData](scan.interface.ts) that streams out new values with each Scan. Each scanner is responsible for supplying raw scan data and determining the type of scan using [OpenposScanType](openpos-scan-type.enum.ts)
