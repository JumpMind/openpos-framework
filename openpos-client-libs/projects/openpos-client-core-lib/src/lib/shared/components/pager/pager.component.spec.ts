import { TestBed, ComponentFixture, tick, fakeAsync } from '@angular/core/testing';
import { PagerComponent } from './pager.component';
import { Component, Input, QueryList, ViewChild } from '@angular/core';

describe('PagerComponent', () => {

    let pagerComponent: PagerComponent;
    let fixture: ComponentFixture<PagerComponent>;
    
    beforeEach( () => {
        TestBed.configureTestingModule({
            declarations: [
                PagerComponent,
                MockIconComponent,
                TestPagerWrapperComponent,
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(PagerComponent);
        pagerComponent = fixture.componentInstance;
        
    });

    describe('constructor', () => {
        it('should have default page state when intially created', () => {
            expect(pagerComponent.pageSize).toBe(5);
            expect(pagerComponent.currentPage).toBe(1);
            expect(pagerComponent.currentIndex).toBe(0);
            expect(pagerComponent.totalPages).toBeUndefined();
        });
    });

    describe('incrementPage', () => {
        it('should not increment page when only 1 page', () => {
            pagerComponent.totalPages = 1;
            pagerComponent.incrementPage();
            expect(pagerComponent.currentIndex).toBe(0);
            expect(pagerComponent.currentPage).toBe(1);
        });

        it('should increment page count and index by 1 when multiple pages', () => {
            pagerComponent.totalPages = 5;
            pagerComponent.incrementPage();
            expect(pagerComponent.currentIndex).toBe(5);
            expect(pagerComponent.currentPage).toBe(2);
            pagerComponent.incrementPage();
            expect(pagerComponent.currentIndex).toBe(10);
            expect(pagerComponent.currentPage).toBe(3);
        });

        it('should not increment page count when page count at last page', () => {
            pagerComponent.totalPages = 5;
            pagerComponent.currentIndex = 20;
            pagerComponent.currentPage = 5;
            pagerComponent.incrementPage();
            expect(pagerComponent.currentIndex).toBe(20);
            expect(pagerComponent.currentPage).toBe(5);
        });
    });

    describe('decrementPage', () => {
        it('should not decrement page when only 1 page', () => {
            pagerComponent.totalPages = 1;
            pagerComponent.decrementPage();
            expect(pagerComponent.currentIndex).toBe(0);
            expect(pagerComponent.currentPage).toBe(1);
        });

        it('should decrement page count and index by 1 when multiple pages', () => {
            pagerComponent.totalPages = 5;
            pagerComponent.currentIndex = 15;
            pagerComponent.currentPage = 3;
            pagerComponent.decrementPage();
            expect(pagerComponent.currentIndex).toBe(10);
            expect(pagerComponent.currentPage).toBe(2);
            pagerComponent.decrementPage();
            expect(pagerComponent.currentIndex).toBe(5);
            expect(pagerComponent.currentPage).toBe(1);
        });

        it('should not decrement page count when page count at first page', () => {
            pagerComponent.totalPages = 5;
            pagerComponent.currentIndex = 0;
            pagerComponent.currentPage = 1;
            pagerComponent.decrementPage();
            expect(pagerComponent.currentIndex).toBe(0);
            expect(pagerComponent.currentPage).toBe(1);
        });
    });
    describe('ngAfterContentInit', () => {
        it('should reset state after sections change', fakeAsync(() => {
            fixture.detectChanges();
            pagerComponent.ngAfterContentInit();
            pagerComponent.currentIndex = 10;
            pagerComponent.currentPage = 2;
            pagerComponent.sections.notifyOnChanges();
            tick();
            expect(pagerComponent.currentIndex).toBe(0);
            expect(pagerComponent.currentPage).toBe(1);
        }));
    });
    

});

describe("PagerComponent - Wrapped", () => {
    describe('ngAfterContentInit', () => {
        let wrapperFixture: ComponentFixture<TestPagerWrapperComponent>;
        let wrappedPagerComponent: PagerComponent;
        beforeEach(() => {
            TestBed.configureTestingModule({
                declarations: [
                    PagerComponent,
                    MockIconComponent,
                    TestPagerWrapperComponent,
                ]
            }).compileComponents();
            wrapperFixture = TestBed.createComponent(TestPagerWrapperComponent);
             wrappedPagerComponent = wrapperFixture.componentInstance.myPager;
        });
        
        it('should setup the page state', () => {
            wrapperFixture.detectChanges();
            wrappedPagerComponent.ngAfterContentInit()
            expect(wrappedPagerComponent.currentIndex).toBe(5);
            expect(wrappedPagerComponent.currentPage).toBe(2);
            expect(wrappedPagerComponent.totalPages).toBe(4);
            wrappedPagerComponent.currentPage = 4;
            wrappedPagerComponent.ngAfterContentInit();
            expect(wrappedPagerComponent.currentIndex).toBe(15);
            expect(wrappedPagerComponent.currentPage).toBe(4);
            expect(wrappedPagerComponent.totalPages).toBe(4);
            
            
        });
    
        it('should ignore out of bounds pages', () => {
            wrapperFixture.detectChanges();
            wrappedPagerComponent.currentPage = 5;
            wrappedPagerComponent.ngAfterContentInit();
            expect(wrappedPagerComponent.currentIndex).toBe(0);
            expect(wrappedPagerComponent.currentPage).toBe(1);
            expect(wrappedPagerComponent.totalPages).toBe(4);
        });
    });
});

@Component({
    selector: 'app-icon',
    template: '',
})
class MockIconComponent {
    @Input() iconName: string;
}

@Component({
    selector: 'test-page',
    template: `
    <app-pager [pageSize]="5" [currentPage]="index">
        <ng-template *ngFor="let number of numbers" #pagerItem>
            <h1>{{number}}</h1>
        </ng-template>
    </app-pager>`
})
class TestPagerWrapperComponent {
    @ViewChild(PagerComponent) myPager;
    numbers = Array(17).fill(0).map((x,i) => i);
    public index = 2;
}