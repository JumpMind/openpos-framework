# Platforms Overview

Platforms represent the container in which the Openpos app runs. Examples would be Cordova, Electron, ReactNative.

All platforms should implement the `IPlatformInterface` and be made `@Injectable`.

## Initialization

Platforms get picked up by the Startup process when a platforms is provided to the `PLATFORMS` injection token.

```typescript
providers: [
    { provide: PLATFORMS, useExisting: CordovaPlatform, multi: true}
    ]
```

The startup task will check if each platform is present and remove it from the `PLATFORMS` injection token if it is not. The startup task will then attempt to initialized each platform in parallel and fail the start up task if any platforms fail. After all platforms initialize the task will complete.

An example initialization for a platform would be waiting for a `deviceready` event.
