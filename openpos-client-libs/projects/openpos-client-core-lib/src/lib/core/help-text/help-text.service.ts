import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, combineLatest} from 'rxjs';
import { IMessageHandler } from '../interfaces/message-handler.interface';
import { SessionService } from '../services/session.service';
import { map, distinctUntilChanged } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
  })
export class HelpTextService implements IMessageHandler<any> {
    private opened$: BehaviorSubject<boolean>;
    private text$: BehaviorSubject<string>;
    private hasText$: Observable<boolean>;
    private initialized$: BehaviorSubject<boolean>;
    private available$: Observable<boolean>;
    private showSideNav$: Observable<boolean>;

    constructor(private sessionService: SessionService) {
        this.opened$ = new BehaviorSubject<boolean>(false);
        this.text$ = new BehaviorSubject<string>(null);
        this.initialized$ = new BehaviorSubject<boolean>(false);
        this.hasText$ = this.text$.pipe(map(text => !!text));
        this.available$ = combineLatest(this.hasText$, this.initialized$, (one, two) => one && two);
        this.showSideNav$ = combineLatest(this.hasText$, this.opened$, (one, two) => one && two);
    }

    handle(message: any) {
        if (!!message.helpText && !!message.helpText.text) {
            this.text$.next(message.helpText.text);
        }
        else if (message.screenType !== 'NoOp') {
            this.text$.next(null);
        }
    }

    public initialize() {
        this.sessionService.registerMessageHandler(this, 'Screen');
        this.initialized$.next(true);
    }

    public open() {
        this.opened$.next(true);
    }

    public close() {
        this.opened$.next(false);
    }

    public toggle() {
        this.opened$.next(!this.opened$.getValue());
    }

    public isOpened() : Observable<boolean> {
        return this.opened$;
    }

    public getText() : Observable<string> {
        return this.text$.pipe(distinctUntilChanged());
    }

    public isAvailable() : Observable<boolean> {
        return this.available$;
    }

    public isSideNavViewable() : Observable<boolean> {
        return this.showSideNav$;
    }
}