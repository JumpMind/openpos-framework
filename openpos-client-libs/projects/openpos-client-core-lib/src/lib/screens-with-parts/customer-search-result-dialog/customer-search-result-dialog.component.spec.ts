import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CustomerSearchResultDialogComponent} from './customer-search-result-dialog.component';
import {ICustomerDetails} from './customer-search-result-dialog.interface';
import {NO_ERRORS_SCHEMA} from '@angular/core'
import {ActionService} from '../../core/actions/action.service';
import {IActionItem} from '../../core/actions/action-item.interface';
import {By} from '@angular/platform-browser';
import {SelectionListInterface} from '../selection-list/selection-list.interface';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../core/client-context/time-zone-context';
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SelectableItemListComponentConfiguration} from '../../shared/components/selectable-item-list/selectable-item-list.component';
import {SelectionMode} from '../../core/interfaces/selection-mode.enum';
import {ActionItem} from '../../core/actions/action-item';

class MockActionService {
    doAction(action: IActionItem) {
    }
};

class MockMatDialog {
};

class MockElectronService {
};

describe('CustomerSearchResultDialogComponent', () => {
    let component: CustomerSearchResultDialogComponent;
    let fixture: ComponentFixture<CustomerSearchResultDialogComponent>;
    let testAddress;
    let testCustomer;
    let testCustomerDisabled;
    let threeResults;
    beforeEach(() => {
        testAddress = {
            line1: 'testStreet',
            line2: 'testStreetLine2',
            city: 'testCity',
            state: 'testState',
            postalCode: '12345'
        };
        testCustomer = {
            name: 'test', loyaltyNumber: '7327', email: 'testUser@test.com',
            phoneNumber: '614 234 5678', address: testAddress, enabled: true, selected: false
        } as ICustomerDetails;
        testCustomerDisabled = {...testCustomer} as ICustomerDetails;
        testCustomerDisabled.enabled = false;
        threeResults = [testCustomer, testCustomer, testCustomerDisabled];
        let ClientContext;
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            declarations: [
                CustomerSearchResultDialogComponent,
            ],
            providers: [
                {provide: ActionService, useClass: MockActionService},
                {provide: MatDialog, useClass: MockMatDialog},
                {provide: ElectronService, useClass: MockElectronService},
                {provide: ClientContext, useValue: {}},
                {provide: CLIENTCONTEXT, useClass: TimeZoneContext}
            ],
            schemas: [
                NO_ERRORS_SCHEMA,
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(CustomerSearchResultDialogComponent);
        component = fixture.componentInstance;
        component.screen = {
            nonSelectionButtons: [{title: 'NonSelectable'}],
            selectionButtons: [{title: 'Select'}, {title: 'View'}]
        } as SelectionListInterface<ICustomerDetails>;
        fixture.detectChanges();
    });

    it('renders', () => {
        expect(component).toBeDefined();
    });

    describe('component', () => {
        beforeEach(() => {
            fixture = TestBed.createComponent(CustomerSearchResultDialogComponent);
            component = fixture.componentInstance;
            component.screen = {
                nonSelectionButtons: [{title: 'NonSelectable'}],
                selectionButtons: [{title: 'Select'}, {title: 'View'}]
            } as SelectionListInterface<ICustomerDetails>;
            fixture.detectChanges();
        });
        it('transform response results to item maps', (done) => {
            component.screen.selectionList = threeResults
            component.buildScreen();
            component.listData.subscribe(
                result => {
                    expect(result.items).toBeTruthy();
                    expect(result.disabledItems).toBeTruthy();
                    expect(result.items.size).toBe(3);
                    expect(result.disabledItems.size).toBe(1);
                    expect(result.items.get(0)).toBe(testCustomer);
                    expect(result.disabledItems.get(2)).toBe(testCustomerDisabled)
                    done();
                },
                () => {
                    fail();
                    done();
                },
                () => {
                    fail();
                    done();
                }
            );
        });

        describe('selection mode functionality', () => {
            let selectedCustomer;
            let listIn;
            beforeEach(() => {
                selectedCustomer = {...testCustomer} as ICustomerDetails;
                selectedCustomer.selected = true;
                listIn = [testCustomer, selectedCustomer]
            });
            it('should not populate selectedItems if multiSelect not specified', function () {
                component.screen.selectionList = threeResults
                component.screen.fetchDataAction = "Something";
                component.buildScreen();
                expect(component.selectedItems).toBeUndefined();
            });
            it('should put selected customers in to selectedItems if multiSelect', function () {
                component.screen.selectionList = listIn
                component.screen.fetchDataAction = undefined
                component.screen.multiSelect = true;
                component.buildScreen();
                expect(component.selectedItems[0]).toBe(selectedCustomer);
                expect(component.indexes.length).toBe(1);
            });

            it('should put selected customers in to selectedItem if not multiSelect', function () {
                component.screen.selectionList = listIn
                component.screen.fetchDataAction = undefined
                component.screen.multiSelect = false;
                component.buildScreen();
                expect(component.selectedItem).toBe(selectedCustomer);
                expect(component.index).not.toBe(-1);
            });
        });

        it('define selection list configuration with 0 items per page', () => {
            component.screen.numberItemsPerPage = 0;
            component.screen.selectionList = [];
            component.screen.multiSelect = false;
            component.buildScreen();
            expect(component.listConfig instanceof SelectableItemListComponentConfiguration).toBeTruthy();
            expect(component.listConfig.numItemsPerPage).toBe(Number.MAX_VALUE);
            expect(component.listConfig.totalNumberOfItems).toBe(component.screen.numberTotalItems);
            expect(component.listConfig.defaultSelectItemIndex).toBe(component.screen.defaultSelectItemIndex);
            expect(component.listConfig.selectionMode).toBe(SelectionMode.Single);
            expect(component.listConfig.fetchDataAction).toBe(component.screen.fetchDataAction);
        });
        it('define selection list configuration with 1000 items per page ', function () {
            component.screen.numberItemsPerPage = 1000;
            component.screen.selectionList = threeResults;
            component.screen.multiSelect = true;
            component.buildScreen();
            expect(component.listConfig instanceof SelectableItemListComponentConfiguration).toBeTruthy();
            expect(component.listConfig.numItemsPerPage).toBe(component.screen.numberItemsPerPage);
            expect(component.listConfig.totalNumberOfItems).toBe(component.screen.selectionList.length);
            expect(component.listConfig.defaultSelectItemIndex).toBe(component.screen.defaultSelectItemIndex);
            expect(component.listConfig.selectionMode).toBe(SelectionMode.Multiple);
            expect(component.listConfig.fetchDataAction).toBe(component.screen.fetchDataAction);
        });
        it('onItemChange should set index', () => {
            component.onItemChange(5);
            expect(component.index).toBe(5);
        });
        it('doSelectionButtonAction should pass on action', () => {
            spyOn(component, 'doAction')
            component.index = 1;
            let testAction = new ActionItem("testAction");
            component.doSelectionButtonAction(testAction);
            expect(component.doAction).toHaveBeenCalledWith(testAction, 1);
        });

        describe('template', () => {
            it('secondary-buttons do exist and have actions', () => {
                const secondaryButton = fixture.debugElement.queryAll(By.css('app-secondary-button'));
                expect(secondaryButton.length).toBe(2);
                const nonSelectionButton = 0;
                const selectionButton = 1;
                expect(secondaryButton[selectionButton].nativeElement.textContent).toBeDefined();
                expect(secondaryButton[nonSelectionButton].nativeElement.textContent).toBeDefined();
                spyOn(component, 'doSelectionButtonAction');
                spyOn(component, 'doNonSelectionButtonAction');
                secondaryButton[selectionButton].nativeElement.click();
                secondaryButton[nonSelectionButton].nativeElement.click();
                expect(component.doSelectionButtonAction).toHaveBeenCalled();
                expect(component.doNonSelectionButtonAction).toHaveBeenCalled();
            });

            it('primary-button do exist and have actions', () => {
                const primaryButton = fixture.debugElement.query(By.css('app-primary-button'));
                expect(primaryButton.nativeElement.textContent).toBeDefined();
                spyOn(component, 'doSelectionButtonAction');
                primaryButton.nativeElement.click()
                expect(component.doSelectionButtonAction).toHaveBeenCalled();
            });
        });
    });
});