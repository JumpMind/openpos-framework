import { Injectable, OnDestroy } from '@angular/core';
import { Observable, merge, Subscription } from 'rxjs';

@Injectable()
export class KeyPressProvider implements OnDestroy {

    private keyPressSources: Observable<KeyboardEvent>[] = [];
    private subscription: Subscription;

    private subscribers = new Map<string, Map<number, KeybindSubscription>>();

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    registerKeyPressSource(source$: Observable<KeyboardEvent>) {
        this.keyPressSources.push(source$);
        this.rebuildKeyPressObserver();
    }

    subscribe(key: string, priority: number, next: (KeyboardEvent) => void): Subscription {

        if (key === null || key === undefined) {
            console.warn('Cannot subscribe to null or undefined keybinding');
            return;
        }

        const subscriptionOutput = new Subscription(() => {
            const priorityMap = this.subscribers.get(key);
            const keybindSubscription = priorityMap.get(priority);
            if (keybindSubscription && keybindSubscription.subscription === subscriptionOutput) {
                priorityMap.delete(priority);
            }
        });

        if (!this.subscribers.has(key)) {
            this.subscribers.set(key, new Map<number, KeybindSubscription>());
        }

        if (this.subscribers.get(key).has(priority)) {
            console.warn(`Another subscriber already exists with key ${key} and priority ${priority}`);
        }
        this.subscribers.get(key).set(priority, new KeybindSubscription(subscriptionOutput, priority, next));

        return subscriptionOutput;
    }

    isSubscribedToKey(key: string): boolean {
        return this.subscribers.has(key);
    }

    private rebuildKeyPressObserver() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }

        this.subscription = merge(...this.keyPressSources).subscribe(event => {
            if (this.subscribers.has(event.key)) {
                const priorityMap = this.subscribers.get(event.key);
                const prioritiesList = Array.from(priorityMap.keys()).sort();
                if (prioritiesList.length > 0) {
                    const priority = prioritiesList[0];
                    this.subscribers.get(event.key).get(priority).event(event);
                }
            }
        });
    }

}

class KeybindSubscription {
    constructor(public subscription: Subscription, public priority: number, public event: (KeyboardEvent) => void) { }
}