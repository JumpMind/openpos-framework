import { Injectable, OnDestroy } from '@angular/core';
import { MessageTypes } from '../messages/message-types';
import { takeUntil } from 'rxjs/operators';
import { LoyaltySignupMessage } from '../messages/loyalty-signup-message';
import { SessionService } from './session.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';

@Injectable({
    providedIn: 'root',
})
export class LoyaltySignupService implements OnDestroy {
    private destroyed$ = new Subject();
    private isActiveOnCustomerDisplay$ = new BehaviorSubject(false);
    private detailsMessage$ = new BehaviorSubject('');

    constructor(private sessionService: SessionService) {
        this.sessionService.getMessages(MessageTypes.LOYALTY_SIGNUP)
            .pipe(takeUntil(this.destroyed$))
            .subscribe((message: LoyaltySignupMessage) => {
                this.isActiveOnCustomerDisplay$.next(message.isActiveOnCustomerDisplay);
                this.detailsMessage$.next(message.detailsMessage);
            });
    }

    checkCustomerDisplayStatus(): void {
        this.sessionService.publish('GetStatus', 'CustomerDisplayLoyaltySignup');
    }

    isActiveOnCustomerDisplay(): Observable<boolean> {
        return this.isActiveOnCustomerDisplay$.asObservable();
    }

    getCustomerDisplayDetailsMessage(): Observable<string> {
        return this.detailsMessage$.asObservable();
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }
}


