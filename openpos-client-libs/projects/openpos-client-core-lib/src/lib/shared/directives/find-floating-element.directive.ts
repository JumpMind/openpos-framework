import { Directive, Host, Self, OnDestroy } from '@angular/core';
import { MatMenuTrigger } from '@angular/material';
import {BehaviorSubject, Subscription} from 'rxjs';
import { FloaterService } from '../../core/services/floater.service';

@Directive({ selector: `[matMenuTriggerFor], [mat-menu-trigger-for]` })
export class FindFloatingElementDirective implements OnDestroy {
    private isFloating$ = new BehaviorSubject<boolean>(false);
    private subscriptions = new Subscription();
    constructor(@Host() @Self() private menu: MatMenuTrigger, private floaterService: FloaterService) {
        this.subscriptions.add(menu.menuClosed.subscribe(() => {
            this.isFloating$.next(false);
        }));
        this.subscriptions.add(menu.menuOpened.subscribe(() => {
            this.isFloating$.next(true);
        }));
        floaterService.pushFloater(this.isFloating$);
    }

    ngOnDestroy(): void {
        this.floaterService.flushFloater(this.isFloating$);
        this.subscriptions.unsubscribe();
    }
}