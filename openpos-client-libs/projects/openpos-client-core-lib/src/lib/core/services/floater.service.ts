import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class FloaterService {
    private floaterList: BehaviorSubject<boolean>[] = [];
    private isFloater$: Observable<boolean>;
    private floatStatus$: BehaviorSubject<boolean>;

    constructor() {
        this.floatStatus$ = new BehaviorSubject<boolean>(false);
    }

    flushFloater(floater: BehaviorSubject<boolean>){
        this.floaterList.splice(this.floaterList.indexOf(floater), 1);
        this.buildFloater();
    }

    pushFloater(floater: BehaviorSubject<boolean>){
        this.floaterList.push(floater);
        this.buildFloater();
    }

    isFloating(): Observable<boolean> {
        this.buildFloater();
        return this.floatStatus$;
    }

    buildFloater(){
        this.isFloater$ = combineLatest(...(this.floaterList.map(f=>f.asObservable())), (...args) => {
            return args.reduce((x,y) =>  x || y);
       });
       this.isFloater$.subscribe((isFloating) => {
            this.floatStatus$.next(isFloating);
       });
    }
}