import {Injectable, OnDestroy} from '@angular/core';
import {MediaService} from '@angular/flex-layout';
import {BreakpointObserver, Breakpoints, BreakpointState} from '@angular/cdk/layout';
import {Observable, Subject} from 'rxjs';
import {startWith, map, filter, takeUntil} from 'rxjs/operators';
import {SessionService} from '../services/session.service';

export class MediaBreakpoints {
    static MOBILE_PORTRAIT = 'mobile-portrait';
    static TABLET_PORTRAIT = 'tablet-portrait';
    static DESKTOP_PORTRAIT = 'desktop-portrait';
    static MOBILE_LANDSCAPE = 'mobile-landscape';
    static TABLET_LANDSCAPE = 'tablet-landscape';
    static DESKTOP_LANDSCAPE = 'desktop-landscape';
    static SMALL_DESKTOP_PORTRAIT = 'small-desktop-portrait';
    static SMALL_DESKTOP_LANDSCAPE = 'small-desktop-landscape';
}

@Injectable({
    providedIn: 'root',
})
export class OpenposMediaService implements OnDestroy {
    breakpointToName = new Map<string, string>();
    destroyed = new Subject();

    constructor(
        private observableMedia: MediaService,
        private breakpointObserver: BreakpointObserver,
        private sessionService: SessionService) {

        sessionService.getMessages('ConfigChanged').pipe(
            filter(message => message.configType === 'MediaService'),
            takeUntil(this.destroyed)
        ).subscribe(message => this.updateBreakpoints(message));
    }

    ngOnDestroy(): void {
        this.destroyed.next();
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
        this.breakpointToName.clear();

        Object.keys(message)
            .filter(configKey => configKey.startsWith('breakpoints'))
            .forEach(configKey => this.addBreakpointToName(message, configKey));
    }

    addBreakpointToName(message: any, configKey: string): void {
        const breakpoint = message[configKey];
        const breakpointName = configKey.split('.')[1];
        this.breakpointToName.set(breakpoint, breakpointName);
    }

    observe<T>(breakpointNameToObject?: Map<string, T>): Observable<T> {
        const breakpointsToWatch = Array.from(this.breakpointToName.keys());

        return this.breakpointObserver.observe(breakpointsToWatch)
            .pipe(
                map(breakpointState => this.mapBreakpointToObject(breakpointState, breakpointNameToObject)),
                takeUntil(this.destroyed)
            );
    }

    mapBreakpointToObject<T>(breakpointState: BreakpointState, breakpointNameToObject: Map<string, T>): T {
        if (!breakpointState.matches) {
            return null;
        }

        const breakpoints = breakpointState.breakpoints;
        let matchedObject = null;

        for (let breakpoint in breakpoints) {
            const isMatched = breakpoints[breakpoint];

            if (isMatched) {
                const breakpointName = this.breakpointToName.get(breakpoint);
                matchedObject = breakpointNameToObject.get(breakpointName);
                break;
            }
        }

        return matchedObject;
    }

    /*
       ** Expects a map of media sizes (xs, s, m, l, xl) to values
       ** Returns an observable that streams out the appropriate values on media size changes
       */
    mediaObservableFromMap<T>(mappedValues: Map<string, T>): Observable<T> {
        const aliases = Array.from(mappedValues.keys());
        const startAlias = aliases.find(alias => this.observableMedia.isActive(alias));

        return this.observableMedia.asObservable()
            .pipe(
                map(change => mappedValues.get(change.mqAlias)),
                startWith(mappedValues.get(startAlias))
            );
    }

}
