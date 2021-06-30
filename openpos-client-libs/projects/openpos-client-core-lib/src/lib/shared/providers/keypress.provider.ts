import {Injectable, OnDestroy} from '@angular/core';
import {fromEvent, merge, Observable, Subject, Subscription} from 'rxjs';
import {filter, map, take, takeUntil, tap} from 'rxjs/operators';
import {Configuration} from '../../configuration/configuration';
import {IActionItem} from '../../core/actions/action-item.interface';
import {LockScreenService} from '../../core/lock-screen/lock-screen.service';

/**
 * How to subscribe to keys, and keys with modifiers:
 *
 * keyPressProvider.subscribe('p', 1, () => this.doAction(...))
 * keyPressProvider.subscribe('ctrl+p', 1, () => this.doAction(...))
 *
 * // Separate multiple keys with a ","
 * keyPressProvider.subscribe('ctrl+p,ctrl+a', 1, () => this.doAction(...))
 *
 * // Escape special keys "," and "+"
 * keyPressProvider.subscribe('shift+\\+', 1, () => this.doAction(...))
 * keyPressProvider.subscribe('shift+\\,', 1, () => this.doAction(...))
 */
@Injectable()
export class KeyPressProvider implements OnDestroy {
    private keyPressSources: Observable<KeyboardEvent>[] = [];
    private subscribers = new Map<string, Map<number, KeybindSubscription>>();
    destroyed$ = new Subject();
    keypressSourceRegistered$ = new Subject<Observable<KeyboardEvent>>();
    keypressSourceUnregistered$ = new Subject<Observable<KeyboardEvent>>();
    stopObserver$ = merge(this.destroyed$, this.keypressSourceRegistered$, this.keypressSourceUnregistered$);
    keyDelimiter = ',';
    keyEscape = '\\';
    keyCombinationChar = '+';
    // Matches a single key
    // p
    // ctrl+p
    keyRegex = new RegExp(/(?<key>(\\\+|[^+])+)/, 'g');

    constructor(private lockScreenService: LockScreenService) {
        merge(
            this.keypressSourceRegistered$,
            this.keypressSourceUnregistered$
        ).subscribe(() => this.rebuildKeyPressObserver());
    }

    globalSubscribe(actions: IActionItem[] | IActionItem): Observable<IActionItem> {
        const actionList = Array.isArray(actions) ? actions : [actions];

        actionList
            .filter(action => action.keybind)
            .forEach(action => {
                const key = this.getNormalizedKey(action.keybind);
                console.log(`[KeyPressProvider]: Globally subscribed to "${key}: ${action.action}"`);
        });

        console.log('[KeyPressProvider]: Subscriptions', this.subscribers);

        return fromEvent(window, 'keydown').pipe(
            map((event: KeyboardEvent) => this.findMatchingAction(actionList, event)),
            // Only notify if a matching action was found
            filter(action => !!action),
            filter(action => this.shouldRunGlobalAction(action)),
            takeUntil(this.destroyed$)
        );
    }

    shouldRunGlobalAction(action: IActionItem): boolean {
        // do we need to check if lock screen is enabled now that we stop propagation?
        const isLockScreenEnabled = this.lockScreenService.enabled$.getValue();

        if (isLockScreenEnabled) {
            const key = this.getNormalizedKey(action.keybind);
            console.warn(`[KeyPressProvider]: Blocking global action "${key}: ${action.action}" because the lock screen is active`);
        }

        return !isLockScreenEnabled && Configuration.enableKeybinds;
    }

    findMatchingAction(actions: IActionItem[], event: KeyboardEvent): IActionItem {
        const eventKey = this.getNormalizedKey(event);
        const eventKeyBinding = this.parse(eventKey)[0];

        return actions
            .filter(action => action.keybind)
            .find(action => {
                // There can be multiple key bindings per action (comma separated, example: ctrl+p,ctrl+a)
                const actionKeyBindings = this.parse(action.keybind);
                return actionKeyBindings.some(keyBinding => this.areEqual(eventKeyBinding, keyBinding));
        });
    }

    areEqual(keyBindingA: Keybinding, keyBindingB: Keybinding): boolean {
        return this.unescapeKey(keyBindingA.key) === this.unescapeKey(keyBindingB.key) &&
            keyBindingA.altKey === keyBindingB.altKey &&
            keyBindingA.ctrlKey === keyBindingB.ctrlKey &&
            keyBindingA.metaKey === keyBindingB.metaKey &&
            keyBindingA.shiftKey === keyBindingB.shiftKey;
    }

    ngOnDestroy(): void {
        this.destroyed$.next();
    }

    registerKeyPressSource(source$: Observable<KeyboardEvent>) {
        this.keyPressSources.push(source$);
        this.keypressSourceRegistered$.next(source$);
    }

    unregisterKeyPressSource(source$: Observable<KeyboardEvent>) {
        const index = this.keyPressSources.indexOf(source$);

        if (index >= 0) {
            this.keyPressSources.splice(index, 1);
        }

        this.keypressSourceUnregistered$.next();
    }

    subscribe(keyOrActionList: string | string[] | IActionItem | IActionItem[],
              priority: number, next: (keyEvent: KeyboardEvent, actionItem?: IActionItem) => void,
              stop$?: Observable<any>, eventType$?: string): Subscription {
        if (!keyOrActionList) {
            console.warn('[KeyPressProvider]: Cannot subscribe to null or undefined or empty string keybinding');
            return null;
        }

        if ( !Configuration.enableKeybinds ) {
            console.info('KeyBinds not enabled skipping subscription');
            return new Subscription();
        }

        let subscriptions;

        // Arrays - Recursively call this function with each item
        if (Array.isArray(keyOrActionList)) {
            subscriptions = (keyOrActionList as any[]).map(keyOrAction => this.subscribe(keyOrAction, priority, next, stop$, eventType$));
        } else {
            // Single item - Register the binding
            const key = typeof keyOrActionList === 'string' ? keyOrActionList : keyOrActionList.keybind;
            const action = typeof keyOrActionList === 'string' ? null : keyOrActionList;
            const keyBindings = this.parse(key);
            if (eventType$) {
                subscriptions = this.registerKeyBindings(keyBindings, action, priority, next, eventType$);
            } else {
                subscriptions = this.registerKeyBindings(keyBindings, action, priority, next, 'keydown');
            }
        }

        const mainSubscription = new Subscription(() => subscriptions.forEach(s => s.unsubscribe()));

        if (stop$) {
            stop$.pipe(take(1)).subscribe(() => mainSubscription.unsubscribe());
        }

        console.log('[KeyPressProvider]: Subscriptions', this.subscribers);

        return mainSubscription;
    }

    registerKeyBindings(keyBindings: Keybinding[], action: IActionItem, priority: number,
                        next: (keyEvent: KeyboardEvent, actionItem?: IActionItem) => void, eventType: string): Subscription[] {

        if (!keyBindings) {
            return [];
        }

        const subscriptions = [];

        keyBindings.forEach(keyBinding => {
            const key = this.getNormalizedKey(keyBinding);
            const subscription = new Subscription(() => {
                const priorityMap = this.subscribers.get(key);
                const keybindSubscription = priorityMap.get(priority);

                if (keybindSubscription && keybindSubscription.subscription === subscription) {
                    priorityMap.delete(priority);
                }

                console.log(`[KeyPressProvider]: Unsubscribing from "${key}" with priority "${priority}"`);
            });

            if (!this.subscribers.has(key)) {
                this.subscribers.set(key, new Map<number, KeybindSubscription>());
            }

            if (this.subscribers.get(key).has(priority)) {
                console.warn(`[KeyPressProvider]: Another subscriber already exists with key "${key}" and priority "${priority}"`);
            } else if (action) {
                console.log(`[KeyPressProvider]: Subscribed to "${key}: ${action.action}" with priority "${priority}"`);
            } else {
                console.log(`[KeyPressProvider]: Subscribed to key "${key}" with priority "${priority}"`);
            }
            this.subscribers.get(key).set(priority, {key, action, subscription, priority, next, eventType});

            subscriptions.push(subscription);
        });

        return subscriptions;
    }

    keyHasSubscribers(obj: KeyboardEvent): boolean {
        const key = this.getNormalizedKey(obj);
        if (this.subscribers.has(key) && this.subscribers.get(key).size > 0) {
            const priorityMap = this.subscribers.get(key);
            const prioritiesList = Array.from(priorityMap.keys())
                    .filter(priority => priorityMap.get(priority).eventType === obj.type);
            return prioritiesList.length > 0;
        }
        return false;
    }

    rebuildKeyPressObserver() {
        merge(...this.keyPressSources).pipe(
            takeUntil(this.stopObserver$)
        ).subscribe(event => {
            const key = this.getNormalizedKey(event);

            if (this.keyHasSubscribers(event)) {
                const priorityMap = this.subscribers.get(key);
                const prioritiesList = Array.from(priorityMap.keys())
                    .filter(priority => priorityMap.get(priority).eventType === event.type).sort();

                if (prioritiesList.length > 0) {
                    const priority = prioritiesList[0];
                    const keybindSubscription = this.subscribers.get(key).get(priority);
                    keybindSubscription.next(event, keybindSubscription.action);
                    event.stopPropagation();
                    event.preventDefault();
                    console.log(`[KeyPressProvider]: Handling "${event.type}" event for "${key}" for element`, event.target);
                }
            } else {
                return;
            }
        });
    }

    getNormalizedKey(obj: KeyboardEvent | Keybinding | string): string {
        const keyBinding = typeof obj === 'string' ? this.parse(obj as string)[0] : obj as Keybinding;
        let normalizedKey = '';

        if (!keyBinding) {
            return normalizedKey;
        }

        // When the user autocompletes the form with the browser there's no "key" property on the KeyboardEvent
        if (!keyBinding.key) {
            return 'browser-autocomplete';
        }

        if (keyBinding.key !== 'Control') {
            normalizedKey += (keyBinding.ctrlKey ? 'ctrl+' : '');
        }
        if (keyBinding.key !== 'Alt') {
            normalizedKey += (keyBinding.altKey ? 'alt+' : '');
        }
        if (keyBinding.key !== 'Shift') {
            normalizedKey += (keyBinding.shiftKey ? 'shift+' : '');
        }
        if (keyBinding.key !== 'Meta') {
            normalizedKey += (keyBinding.metaKey ? 'meta+' : '');
        }
        normalizedKey += this.escapeKey(keyBinding.key);

        return normalizedKey.toLowerCase();
    }

    parse(key: string): Keybinding[] {
        if (!key) {
            return null;
        }

        const keys = this.splitKeys(key);
        const keyBindings = [];

        keys.forEach(theKey => {
            const keyParts = Array.from(theKey['matchAll'](this.keyRegex)).map((value: RegExpMatchArray) => value.groups.key);

            if (keyParts.length === 0) {
                return;
            }

            const keyBinding: Keybinding = {
                key: this.unescapeKey(keyParts[keyParts.length - 1].toLowerCase())
            };

            for (let i = 0; i < keyParts.length - 1; i++) {
                const keyPart = keyParts[i].toLowerCase();

                // Being flexible with how developers want to specify key modifiers
                switch (keyPart) {
                    case 'command':
                    case 'cmd':
                    case 'mta':
                    case 'met':
                    case 'meta':
                        keyBinding.metaKey = true;
                        break;
                    case 'alt':
                    case 'opt':
                    case 'option':
                    case 'optn':
                        keyBinding.altKey = true;
                        break;
                    case 'ctl':
                    case 'ctr':
                    case 'ctrl':
                    case 'control':
                        keyBinding.ctrlKey = true;
                        break;
                    case 'shft':
                    case 'sft':
                    case 'shift':
                        keyBinding.shiftKey = true;
                        break;
                }
            }

            keyBindings.push(keyBinding);
        });

        return keyBindings;
    }

    /**
     * Splits key presses separated by a comma
     * @param keys The set of one or more keys to split
     * @example
     * p
     * ctrl+p
     * ctrl+p,ctrl+a,cmd+\,,p
     */
    splitKeys(keys: string): string[] {
        const keyPressList = [];
        let keyBuffer = '';

        for (let i = 0; i < keys.length; i++) {
            const char = keys[i];
            const nextChar = keys[i + 1];

            // If the delimiter is escaped, treat it as a key
            if (char === this.keyEscape && (nextChar === this.keyDelimiter || nextChar === this.keyCombinationChar)) {
                keyBuffer += this.keyEscape + nextChar;
                i++;
            // If we've reached the delimiter, and there's stuff in the buffer, add the buffer to the key list and flush buffer
            } else if (char === this.keyDelimiter && keyBuffer) {
                keyPressList.push(keyBuffer);
                keyBuffer = '';
            // Add the char to the key buffer
            } else {
                keyBuffer += char;
            }
        }

        // Add what's left in the key buffer to the list
        if (keyBuffer) {
            keyPressList.push(keyBuffer);
        }

        return keyPressList;
    }

    unescapeKey(key: string): string {
        return key.startsWith('\\') ? key.substr(1) : key;
    }

    escapeKey(key: string): string {
        if (key && !key.startsWith(this.keyEscape) && (key === this.keyCombinationChar || key === this.keyDelimiter)) {
            return this.keyEscape + key;
        }

        return key;
    }
}

export interface KeybindSubscription {
    key: string;
    action: IActionItem;
    subscription: Subscription;
    priority: number;
    eventType: string;
    next: (keyEvent: KeyboardEvent, actionItem?: IActionItem) => void;
}

export interface Keybinding {
    key: string;
    ctrlKey?: boolean;
    altKey?: boolean;
    shiftKey?: boolean;
    metaKey?: boolean;
}
