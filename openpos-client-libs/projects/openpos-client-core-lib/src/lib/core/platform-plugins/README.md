# Platform Plugins

Platform plugins are OpenPOS representations of plugins or features that are in the platform OpenPOS runs on. Examples couple be a plugin for a scanner or payment device.

All platform plugins should implement [IPlatformPlugin](platform-plugin.interface.ts) and be made `@Injectable`.

## Initialization

Platform plugins get picked up the Startup process when it is provided to the `PLUGINS` injection token.

```typescript
providers: [
    { provide: PLUGINS, useExisting: InfineaScannerCordovaPlugin, multi: true },
    ]
```

The startup task will check if each platform plugin is present and remove it from the `PLUGINS` injection token if it is not. The startup task will then attempt to initialized each platform plugin in parallel and fail the start up task if any platform plugins fail. After all platforms plugins initialize the task will complete.
