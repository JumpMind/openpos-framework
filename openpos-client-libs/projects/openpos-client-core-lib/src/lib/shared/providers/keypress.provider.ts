import {Injectable, OnDestroy} from '@angular/core';
import {fromEvent, merge, Observable, Subject, Subscription} from 'rxjs';
import {filter, map, take, takeUntil, tap} from 'rxjs/operators';
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
 * keyPressProvider.subscribe('shift+\+', 1, () => this.doAction(...))
 * keyPressProvider.subscribe('shift+\,', 1, () => this.doAction(...))
 */
@Injectable()
export class KeyPressProvider implements OnDestroy {
    private keyPressSources: Observable<KeyboardEvent>[] = [];
    private subscribers = new Map<string, Map<number, KeybindSubscription>>();
    destroyed$ = new Subject();
    keypressSourceRegistered$ = new Subject<Observable<KeyboardEvent>>();
    keypressSourceUnregistered$ = new Subject<Observable<KeyboardEvent>>();
    stopObserver$ = merge(this.destroyed$, this.keypressSourceRegistered$, this.keypressSourceUnregistered$);
    // Matches key lists, with keys optionally separated by a ","
    // ctrl+p
    // ctrl+p,ctrl+a,p
    keyListRegex = new RegExp(/(?<key>(\\,|[^,])+)((?<!\\),)?/, 'g');
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

        actionList.forEach(action => {
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
        const isLockScreenEnabled = this.lockScreenService.enabled.getValue();

        if(isLockScreenEnabled) {
            const key = this.getNormalizedKey(action.keybind);
            console.warn(`[KeyPressProvider]: Blocking global action "${key}: ${action.action}" because the lock screen is active`);
        }

        return !isLockScreenEnabled;
    }

    findMatchingAction(actions: IActionItem[], event: KeyboardEvent): IActionItem {
        const eventKey = this.getNormalizedKey(event);
        const eventKeyBinding = this.parse(eventKey)[0];

        return actions.find(action => {
            // There can be multiple key bindings per action (comma separated, example: ctrl+p,ctrl+a)
            const actionKeyBindings = this.parse(action.keybind);
            return actionKeyBindings.some(keyBinding => this.areEqual(eventKeyBinding, keyBinding));
        });
    }

    areEqual(keyBindingA: Keybinding, keyBindingB: Keybinding): boolean {
        return keyBindingA.key === keyBindingB.key &&
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

    subscribe(keyOrActionList: string | string[] | IActionItem | IActionItem[], priority: number, next: (KeyboardEvent, IActionItem?) => void, stop$?: Observable<any>): Subscription {
        if (!keyOrActionList) {
            console.warn('[KeyPressProvider]: Cannot subscribe to null or undefined or empty string keybinding');
            return;
        }

        let subscriptions;

        // Arrays - Recursively call this function with each item
        if(Array.isArray(keyOrActionList)) {
            subscriptions = (keyOrActionList as any[]).map(keyOrAction => this.subscribe(keyOrAction, priority, next, stop$));
        } else {
            // Single item - Register the binding
            const key = typeof keyOrActionList === 'string' ? keyOrActionList : keyOrActionList.keybind;
            const action = typeof keyOrActionList === 'string' ? null : keyOrActionList;
            const keyBindings = this.parse(key);
            subscriptions = this.registerKeyBindings(keyBindings, action, priority, next);
        }

        const mainSubscription = new Subscription(() => subscriptions.forEach(s => s.unsubscribe()));

        if (stop$) {
            stop$.pipe(take(1)).subscribe(() => mainSubscription.unsubscribe());
        }

        console.log('[KeyPressProvider]: Subscriptions', this.subscribers);

        return mainSubscription;
    }

    registerKeyBindings(keyBindings: Keybinding[], action: IActionItem, priority: number, next: (KeyboardEvent, IActionItem?) => void): Subscription[] {
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
            } else if(action) {
                console.log(`[KeyPressProvider]: Subscribed to "${key}: ${action.action}" with priority "${priority}"`);
            } else {
                console.log(`[KeyPressProvider]: Subscribed to key "${key}" with priority "${priority}"`);
            }
            this.subscribers.get(key).set(priority, {key, action, subscription, priority, next});

            subscriptions.push(subscription);
        });

        return subscriptions;
    }

    keyHasSubscribers(obj: KeyboardEvent | Keybinding | string): boolean {
        const key = this.getNormalizedKey(obj);
        return this.subscribers.has(key) && this.subscribers.get(key).size > 0;
    }

    rebuildKeyPressObserver() {
        merge(...this.keyPressSources).pipe(
            takeUntil(this.stopObserver$)
        ).subscribe(event => {
            const key = this.getNormalizedKey(event);

            if (!this.subscribers.has(key)) {
                return;
            }

            const priorityMap = this.subscribers.get(key);
            const prioritiesList = Array.from(priorityMap.keys()).sort();

            if (prioritiesList.length > 0) {
                const priority = prioritiesList[0];
                const keybindSubscription = this.subscribers.get(key).get(priority);
                keybindSubscription.next(event, keybindSubscription.action);
            }
        });
    }

    getNormalizedKey(obj: KeyboardEvent | Keybinding | string): string {
        let keyBinding = typeof obj === 'string' ? this.parse(obj as string)[0] : obj as Keybinding;
        let normalizedKey = '';

        if(!keyBinding) {
            return normalizedKey;
        }

        if(keyBinding.key !== 'Control') {
            normalizedKey += (keyBinding.ctrlKey ? 'ctrl+' : '');
        }
        if(keyBinding.key !== 'Alt') {
            normalizedKey += (keyBinding.altKey ? 'alt+' : '');
        }
        if(keyBinding.key !== 'Shift') {
            normalizedKey += (keyBinding.shiftKey ? 'shift+' : '');
        }
        if(keyBinding.key !== 'Meta') {
            normalizedKey += (keyBinding.metaKey ? 'meta+' : '');
        }
        normalizedKey += keyBinding.key;

        return normalizedKey.toLowerCase();
    }

    parse(key: string): Keybinding[] {
        if (!key) {
            return null;
        }

        const keys = Array.from(key['matchAll'](this.keyListRegex)).map((value: RegExpMatchArray) => value.groups.key);
        const keyBindings = [];

        keys.forEach(theKey => {
            const keyParts = Array.from(theKey['matchAll'](this.keyRegex)).map((value: RegExpMatchArray) => value.groups.key);
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

    unescapeKey(key: string): string {
        return key.startsWith('\\') ? key.substr(1) : key;
    }

    escapeKey(key: string): string {
        return !key.startsWith('\\') ? `\\${key}` : key;
    }
}

export interface KeybindSubscription {
    key: string;
    action: IActionItem;
    subscription: Subscription;
    priority: number;
    next: (KeyboardEvent, IActionItem?) => void;
}

export interface Keybinding {
    key: string;
    ctrlKey?: boolean;
    altKey?: boolean;
    shiftKey?: boolean;
    metaKey?: boolean;
}