import { InjectionToken } from '@angular/core';

export const CLIENTCONTEXT = new InjectionToken<IClientContext[]>('ClientContext');

export interface IClientContext {
    getContextProperties(): Map<string, string>;
}
