import { Injectable } from '@angular/core';
import { MediaService } from '@angular/flex-layout';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { startWith, map, filter } from 'rxjs/operators';
import { SessionService } from '../services/session.service';

export class MediaBreakpoints {
    static MOBILE_PORTRAIT = 'mobile-portrait';
    static TABLET_PORTRAIT = 'tablet-portrait';
    static DESKTOP_PORTRAIT = 'desktop-portrait';
    static MOBILE_LANDSCAPE = 'mobile-landscape';
    static TABLET_LANDSCAPE = 'tablet-landscape';
    static DESKTOP_LANDSCAPE = 'desktop-landscape';
}

@Injectable({
    providedIn: 'root',
})
export class OpenposMediaService {

    private _breakpointToName = new Map([
        [Breakpoints.HandsetPortrait, MediaBreakpoints.MOBILE_PORTRAIT],
        [Breakpoints.TabletPortrait, MediaBreakpoints.TABLET_PORTRAIT],
        [Breakpoints.WebPortrait, MediaBreakpoints.DESKTOP_PORTRAIT],
        [Breakpoints.HandsetLandscape, MediaBreakpoints.MOBILE_LANDSCAPE],
        [Breakpoints.TabletLandscape, MediaBreakpoints.TABLET_LANDSCAPE],
        [Breakpoints.WebLandscape, MediaBreakpoints.DESKTOP_LANDSCAPE]
    ]);

    public get breakpointToName(): Map<string, string> {
        return this._breakpointToName;
    }

    constructor(
        private observableMedia: MediaService,
        private breakpointObserver: BreakpointObserver,
        private sessionService: SessionService) {

        sessionService.getMessages('ConfigChanged').pipe(
            filter(m => m.configType === 'MediaService')
        ).subscribe(message => {
            this.updateBreakpoints(message);
        });
    }

    /*
    Breakpoints can be overriden with the following client config in application.yml:

    clientConfiguration:
      clientConfigSets:
      - tags: default
        configsForTags:
          MediaService:
             breakpoints:
               'mobile-portrait': '(max-width: 599.99px) and (orientation: portrait)'
               'mobile-landscape': '(max-width: 959.99px) and (orientation: landscape)'
               'tablet-portrait': '(min-width: 600px) and (max-width: 839.99px) and (orientation: portrait)'
               'tablet-landscape': '(min-width: 960px) and (max-width: 1279.99px) and (orientation: landscape)'
               'desktop-portrait': '(min-width: 840px) and (orientation: portrait)'
               'desktop-landscape': '(min-width: 1280px) and (orientation: landscape)'
    */
    private updateBreakpoints(message: any) {
        for (const configName of Object.keys(message)) {
            if (configName.startsWith('breakpoints')) {
                const breakpointName = configName.replace('breakpoints\.', '');
                this.breakpointToName.forEach((name, breakpoint) => {
                    if (name === breakpointName) {
                        this.breakpointToName.delete(breakpoint);
                    }
                });
                this.breakpointToName.set(message[configName], breakpointName);
            }
        }
    }

    observe<T>(nameToObject: Map<string, T>): Observable<T> {
        return this.breakpointObserver.observe(
            Array.from(this.breakpointToName.keys())
        ).pipe(
            map(state => {
                let returnObj: T;
                Object.keys(state.breakpoints).forEach(breakpoint => {
                    if (state.breakpoints[breakpoint]) {
                        const name = this.breakpointToName.get(breakpoint);
                        returnObj = nameToObject.get(name);
                    }
                });
                return returnObj;
            })
        );
    }

    /*
    ** Expects a map of media sizes (xs, s, m, l, xl) to values
    ** Returns an observable that streams out the appropriate values on media size changes
    */
    mediaObservableFromMap<T>(mappedValues: Map<string, T>): Observable<T> {

        let startValue: T;
        mappedValues.forEach((value, mqAlias) => {
            if (this.observableMedia.isActive(mqAlias)) {
                startValue = value;
            }
        });

        return this.observableMedia.asObservable().pipe(
            map(change => mappedValues.has(change.mqAlias) ? mappedValues.get(change.mqAlias) : mappedValues.get('')),
            startWith(startValue)
        );
    }

}
