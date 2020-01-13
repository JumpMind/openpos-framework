import {Component, Injector} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {IActionItem} from '../../core/actions/action-item.interface';
import {ActionService} from '../../core/actions/action.service';
import {IAbstractScreen} from '../../core/interfaces/abstract-screen.interface';
import {PosScreen} from './pos-screen.component';

interface ITestScreen extends IAbstractScreen{
    testProp: string;
}

@Component({
    template:''
})
class TestComponent extends PosScreen<ITestScreen>{
    public buildScreenCalled = false;
    constructor(injector: Injector) {
        super(injector);
    }

    buildScreen() {
        this.buildScreenCalled = true;
    }
}

describe('pos-screen', () => {

    let actionService: jasmine.SpyObj<ActionService>;
    let fixture: ComponentFixture<TestComponent>;
    let testComponent: TestComponent;

    beforeEach(() => {

        const actionServiceSpy = jasmine.createSpyObj('ActionService', ['doAction'])

        TestBed.configureTestingModule( {
            declarations:[
                TestComponent
            ],
            providers: [
                {provide: ActionService, useValue: actionServiceSpy}
            ]
        });
        actionService = TestBed.get(ActionService);
        fixture = TestBed.createComponent(TestComponent);
        testComponent  = fixture.componentInstance;
    });

    it('should successfully create', () => {
        expect(testComponent).toBeTruthy();
    });

    it( 'should call build screen', () => {
        testComponent.show( {testProp: 'test'} as ITestScreen);

        expect(testComponent.buildScreenCalled).toBeTruthy();
    });

    it('should copy over all the props to the existing screen obj when show is called', () => {

        let testScreen = {testProp: 'test'} as ITestScreen;
        testComponent.show( testScreen );

        expect(testComponent.screen).toBeTruthy();

        // Screen object should be a copy and not the same object so that we don't trigger change detection on every screen update
        expect(testComponent.screen).not.toBe(testScreen);
        expect(testComponent.screen).toEqual(testScreen);
    });

    it('should call the action service when do action is called with string action', () => {
        testComponent.doAction('testAction', 'payload');

        expect(actionService.doAction).toHaveBeenCalledWith({action:'testAction'}, 'payload');
    });

    it('should call the action service when do action is called with and IActionItem', () => {
        let testAction = {action: 'testAction'} as IActionItem;

        testComponent.doAction(testAction, 'payload');

        expect(actionService.doAction).toHaveBeenCalledWith(testAction, 'payload');
    });
});