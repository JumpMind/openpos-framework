import { Component, TemplateRef, QueryList, ContentChildren, Input, AfterContentInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-pager',
    templateUrl: './pager.component.html',
    styleUrls: ['./pager.component.scss']
})

export class PagerComponent implements AfterContentInit, OnDestroy {
    @ContentChildren('pagerItem') sections: QueryList<TemplateRef<any>>;

    @Input() pageSize = 5;
    @Input() currentPage = 1;
    @Output() currentPageChange = new EventEmitter();
    totalPages: number;
    currentIndex = 0;
    private subscription: Subscription;
    

    ngAfterContentInit(): void {
        this._initialPageSetup();
        if (this.sections) {
            this.subscription = this.sections.changes.subscribe( () => {
                    this._resetPageState();
            });
        }
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    private _resetPageState(): void {
        this.currentPage = 1;
        this.currentPageChange.emit(this.currentPage);
        this.currentIndex = 0;
        this.totalPages = Math.ceil(this.sections.length / this.pageSize);
    }

    incrementPage() {
        if (this.currentPage !== this.totalPages) {
            this.currentIndex += this.pageSize;
            this.currentPage++;
            this.currentPageChange.emit(this.currentPage);
        }
    }

    private _initialPageSetup(): void {
        this.totalPages = Math.ceil(this.sections.length / this.pageSize);
        this.currentIndex = 0;
        if(this.currentPage != 1){
            this.currentPage = this.currentPage > 0 && this.currentPage <= this.totalPages ? this.currentPage : 1;
            this.currentPageChange.emit(this.currentPage);
            if(this.currentPage != 1){
                this.currentIndex = (this.currentPage - 1) * this.pageSize;
            }
        }
    }

    decrementPage() {
        if (this.currentPage !== 1) {
            this.currentIndex -= this.pageSize;
            this.currentPage--;
            this.currentPageChange.emit(this.currentPage);
        }
    }
}
