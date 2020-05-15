import {Injectable, OnDestroy} from '@angular/core';
import {merge, Observable, Subject, Subscription} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Injectable()
export class KeyPressProvider implements OnDestroy {
    private keyPressSources: Observable<KeyboardEvent>[] = [];
    private subscribers = new Map<string, Map<number, KeybindSubscription>>();
    destroyed$ = new Subject();
    keypressSourceRegistered$ = new Subject<Observable<KeyboardEvent>>();
    keypressSourceUnregistered$ = new Subject<Observable<KeyboardEvent>>();
    stopObserver$ = merge(this.destroyed$, this.keypressSourceRegistered$, this.keypressSourceUnregistered$);

    constructor() {
        merge(
            this.keypressSourceRegistered$,
            this.keypressSourceUnregistered$
        ).subscribe(() => this.rebuildKeyPressObserver());
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

    subscribe(key: string, priority: number, next: (KeyboardEvent) => void): Subscription {
        if (!key) {
            console.warn('[KeyPressProvider]: Cannot subscribe to null or undefined or empty string keybinding');
            return;
        }

        key = this.getNormalizedKey(key);

        const subscriptionOutput = new Subscription(() => {
            const priorityMap = this.subscribers.get(key);
            const keybindSubscription = priorityMap.get(priority);

            if (keybindSubscription && keybindSubscription.subscription === subscriptionOutput) {
                priorityMap.delete(priority);
            }

            console.log(`[KeyPressProvider]: Unsubscribing from "${key}" and priority "${priority}"`);
        });

        if (!this.subscribers.has(key)) {
            this.subscribers.set(key, new Map<number, KeybindSubscription>());
        }

        if (this.subscribers.get(key).has(priority)) {
            console.warn(`[KeyPressProvider]: Another subscriber already exists with key "${key}" and priority "${priority}"`);
        } else {
            console.log(`[KeyPressProvider]: Subscribed to key "${key}" and priority "${priority}"`);
        }
        this.subscribers.get(key).set(priority, new KeybindSubscription(key, subscriptionOutput, priority, next));

        return subscriptionOutput;
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
                this.subscribers.get(key).get(priority).event(event);
            }
        });
    }

    getNormalizedKey(obj: KeyboardEvent | Keybinding | string): string {
        let keyBinding = typeof obj === 'string' ? this.parse(obj as string) : obj as Keybinding;
        let normalizedKey = '';

        normalizedKey += (keyBinding.ctrlKey ? 'ctrl+' : '');
        normalizedKey += (keyBinding.altKey ? 'alt+' : '');
        normalizedKey += (keyBinding.shiftKey ? 'shift+' : '');
        normalizedKey += (keyBinding.metaKey ? 'meta+' : '');
        normalizedKey += keyBinding.key.toLowerCase();

        return normalizedKey;
    }

    parse(key: string): Keybinding {
        if (!key) {
            return null;
        }

        const parts = key.split('+');
        const keyBinding: Keybinding = {
            key: parts[parts.length - 1].toLowerCase()
        };

        for (let i = 0; i < parts.length - 1; i++) {
            const part = parts[i].toLowerCase();

            // Being flexible with how developers want to specify key modifiers
            switch (part) {
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

        return keyBinding;
    }
}

class KeybindSubscription {
    constructor(
        public key: string,
        public subscription: Subscription,
        public priority: number,
        public event: (KeyboardEvent) => void) {
    }
}

interface Keybinding {
    key: string;
    ctrlKey?: boolean;
    altKey?: boolean;
    shiftKey?: boolean;
    metaKey?: boolean;
}