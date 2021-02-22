import { Injectable } from '@angular/core';
import { Plugins as CapacitorPlugins, Capacitor } from '@capacitor/core';

import { Observable, of, throwError } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { IPlatformPlugin } from '../../platform-plugin.interface';

import { ImageScanner, ScannerViewRef, ScanData } from '../scanner';

@Injectable({
    providedIn: 'root'
}) 
//@ImageScannerDef('scandit-cap')
export class ScanditCapacitorImageScanner implements ImageScanner, IPlatformPlugin {
    name(): string {
        return 'scandit-cap';
    }
    
    pluginPresent(): boolean {
        return Capacitor.isPluginAvailable('ScanditNative');
    }

    initialize(): Observable<string> {
        return of(CapacitorPlugins.ScanditNative.initialize({
            // todo: this is just a temporary trial key... needs to be changed when the initialization
            // is working. This is relaltively safe for now.
            apiKey: 'Afe/bGYVRrCYPHgW8TFeSSwGXJXGAYbWNmSmhplfA3pfehxgZFR1Krh6oj4EKQmn2F8D0yBxTgM6Hal4/G4kdYBeXJknZfBYWwTWANVSxJKbdH3Uf2KSxtRdEf4YQaWfVEuld6lQlrM+LpumXznPq8ItcngxL9D+FFAMeuD7YaKyN10S3cJ7ziZmwln+y9Cbc8KctlQt3XFMI84y2YDFsG/VBKn0ys6cE9HYKc1DET6GDGmCabylblsWEzgDRiYguUnH0YWZ5cCmc6nhk5sdUc0FtE6CgiM2G+bMl+KWBPWxCfE8Jeg6D9Cj+9nSTtuqHgVIHseN/hFiGkQvsAZEzJJWdspNJV5k5k9g+6uoBC2P2B+y8RU0wM9wYHeyQkNuVjlmpktPhz3jMR6IqRfyXZuxiSY7S4hmpAcIIO7gJLlSSd6YP8qd3C6s5Sv6hXJ0VihRH+oSL8MOL/b/b9WDvwqb2SsURdMx7Q1HYWH1Ng1BuEb/sbtO1+Bzk7hLXbKc4Txe1B49NWf6HtQMyy70CPqvrmx+NLxQOacehqVeselC3XvZcKt3GoO8CuaXU0ZnG8Xg8ibTZELy4yoaFPeXYkT7K1e/8HELhpn0clXm6XfQMAlsn2XNUfoJs+x9q0FyBeFb3NRxAsKtEpd+anShSSTtywHauZtuwDlM9lUbCj/l+oaZRPPsYImrhPMHI+NVHAP5B+i4LLw2yXqCdxBX+83ycsIQ5Rktm2DtXNpX0cJ1M0V4YdSw61bbQR+xnYDiVHVruw6Dh+mNOkWLpLJwz/AOLL46598f/AIsAEl+HAoiAglYpH7YLco0bAe1JZVSEDji3Naa7wM2'
        })).pipe(
            map(() => "Initialized Scandit for Capacitor")
        );
    }

    beginScanning(view: ScannerViewRef): Observable<ScanData> {
        if (!Capacitor.isPluginAvailable('ScanditNative')) {
            return throwError('the scandit plugin is not available');
        }

        return new Observable(observer => {
            CapacitorPlugins.ScanditNative.addView();

            const updateViewSub = view.viewChanges().pipe(
                mergeMap(d => CapacitorPlugins.ScanditNative.updateView(d))
            ).subscribe();

            const handle = CapacitorPlugins.ScanditNative.addListener('scan', (e) => {
                observer.next({
                    type: e.symbology,
                    data: e.data
                });
            });

            return () => {
                handle.remove();
                updateViewSub.unsubscribe();
                CapacitorPlugins.ScanditNative.removeView();
            };
        });
    }
}
