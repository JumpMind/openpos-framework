import { InjectionToken } from '@angular/core';

// Create injection tokens for the training overlay to avoid circular dependencies
export const INSTRUCTIONS_DATA = new InjectionToken<{}>('INSTRUCTIONS_DATA');
