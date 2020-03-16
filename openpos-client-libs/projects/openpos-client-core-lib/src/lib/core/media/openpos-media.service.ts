import {Injectable, OnDestroy} from '@angular/core';
import {MediaService} from '@angular/flex-layout';
import {BreakpointObserver, BreakpointState} from '@angular/cdk/layout';
import {BehaviorSubject, merge, Observable, Subject} from 'rxjs';
import {debounceTime, filter, map, startWith, takeUntil, tap} from 'rxjs/operators';
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

export interface MediaBreakpoint {
    name: string;
    value: string;
}

/*
    Breakpoints can be set with the following client config in application.yml:

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
               'small-desktop-portrait': '(min-width: 768px) and (max-width: 768px) and (orientation: portrait)'
               'small-desktop-landscape': '(min-width: 1366px) and (max-width: 1366px) and (orientation: landscape)'
    */
@Injectable({
    providedIn: 'root',
})
export class OpenposMediaService implements OnDestroy {
    breakpointToName = new Map<string, string>();
    activeBreakpoint$ = new BehaviorSubject<MediaBreakpoint>(null);
    configChanged$ = new Subject();
    destroyed$ = new Subject();
    logDebounceTime = 100;

    constructor(
        private mediaService: MediaService,
        private breakpointObserver: BreakpointObserver,
        private sessionService: SessionService) {

        this.logChanges();
        this.watchForConfigChanges();
    }

    ngOnDestroy(): void {
        this.destroyed$.next();
    }

    /**
     * Logs configuration and breakpoint changes.
     */
    logChanges(): void {
        this.configChanged$.pipe(debounceTime(this.logDebounceTime))
            .subscribe(message => this.log('Configuration Changed', message));

        this.activeBreakpoint$.pipe(debounceTime(this.logDebounceTime))
            .subscribe(mediaBreakpoint => this.log('Active Breakpoint', mediaBreakpoint));
    }

    /**
     * Updates the the breakpoints when the configuration changes.
     */
    watchForConfigChanges(): void {
        this.sessionService.getMessages('ConfigChanged')
            .pipe(
                filter(message => message.configType === 'MediaService'),
                tap(message => this.updateBreakpointsFromConfig(message)),
                tap(() => this.configChanged$.next()),
                tap(() => this.watchForBreakpointChanges()),
                takeUntil(this.destroyed$)
            ).subscribe();
    }

    /**
     * Updates the available breakpoints from the configuration.
     * @param message The configuration message
     */
    updateBreakpointsFromConfig(message: any) {
        this.breakpointToName.clear();

        Object.keys(message)
            .filter(configKey => configKey.startsWith('breakpoints'))
            .forEach(configKey => this.addBreakpointFromConfig(message, configKey));
    }

    /**
     * Adds a breakpoint from a given configuration.
     * @param message The configuration
     * @param configKey The configuration key to add
     */
    addBreakpointFromConfig(message: any, configKey: string): void {
        const breakpoint = message[configKey];
        // Remove the path part of the configuration key
        // Example: breakpoints.desktop-landscape => desktop-landscape
        const breakpointName = configKey.split('.')[1];
        this.breakpointToName.set(breakpoint, breakpointName);
    }

    /**
     * Notifies listeners when the active breakpoint changes.
     */
    watchForBreakpointChanges(): void {
        const breakpointsToWatch = Array.from(this.breakpointToName.keys());

        this.breakpointObserver.observe(breakpointsToWatch)
            .pipe(
                filter(breakpointState => breakpointState.matches),
                map(breakpointState => this.getActiveMediaBreakpoint(breakpointState)),
                tap(mediaBreakpoint => this.activeBreakpoint$.next(mediaBreakpoint)),
                takeUntil(merge(this.configChanged$, this.destroyed$))
            ).subscribe();
    }

    /**
     * Observe changes in the active breakpoint and get notified with custom values.
     * @param breakpointNameToObject A map of values to use for notifying when the active breakpoint changes
     */
    observe<T>(breakpointNameToObject?: Map<string, T>): Observable<T> {
        return this.activeBreakpoint$
            .pipe(
                filter(mediaBreakpoint => !!mediaBreakpoint),
                map(mediaBreakpoint => mediaBreakpoint.name),
                map(breakpointName => breakpointNameToObject.get(breakpointName))
            );
    }

    /**
     * Gets the active media breakpoint from a breakpoint state.
     * @param breakpointState The breakpoint state
     */
    getActiveMediaBreakpoint(breakpointState: BreakpointState): MediaBreakpoint {
        const breakpoints = breakpointState.breakpoints;
        const breakpointNames = Object.keys(breakpoints);
        const activeBreakpoint = breakpointNames.find(breakpoint => breakpoints[breakpoint]);

        if (!activeBreakpoint) {
            return null;
        }

        const activeBreakpointName = this.breakpointToName.get(activeBreakpoint);

        return {
            name: activeBreakpointName,
            value: activeBreakpoint
        };
    }

    /**
     * Returns an observable that streams out a set of custom values that map to active breakpoints.
     * @param mappedValues A map of media sizes (xs, s, m, l, xl) to custom values
     */
    mediaObservableFromMap<T>(mappedValues: Map<string, T>): Observable<T> {
        const aliases = Array.from(mappedValues.keys());
        const startAlias = aliases.find(alias => this.mediaService.isActive(alias));

        return this.mediaService.asObservable()
            .pipe(
                map(change => mappedValues.get(change.mqAlias)),
                startWith(mappedValues.get(startAlias))
            );
    }

    /**
     * Logs messages and includes the service name in the messages.
     * @param args The arguments to log
     */
    log(...args): void {
        console.log.apply(console, ['Media Service:'].concat(args));
    }
}
