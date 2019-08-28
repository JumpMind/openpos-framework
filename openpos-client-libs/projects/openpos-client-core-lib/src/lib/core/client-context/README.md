# Client Context

ClientContext are properties that describe a client when it subscribes. Things like deviceId, timezone, platform ...

## Extending Client Context

Additional client context properties beyond what is provided by the core can be added by using extending [IClientContext](client-context-provider.interface.ts) and providing it with the `CLIENTCONTEXT` injection token.

```typescript
    providers: [
        { provide: CLIENTCONTEXT, useClass: TimeZoneContext, multi: true }
    ]
```

These properties will be added as headers to the client subscription and made available on the server.
