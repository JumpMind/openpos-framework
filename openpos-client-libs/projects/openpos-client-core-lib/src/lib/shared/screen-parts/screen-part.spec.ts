import { TestBed } from '@angular/core/testing';
import { cold, getTestScheduler } from 'jasmine-marbles';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { ScreenPartComponent } from './screen-part';
import { Component } from '@angular/core';
import { ScreenPart } from '../decorators/screen-part.decorator';
import { MessageProvider } from '../providers/message.provider';

interface TestPartInterface extends IAbstractScreen {
    testProperty: string;
}

@ScreenPart({
    name: 'TestPart'
})
@Component({
    template: 'hi',
})
class TestPartComponent extends ScreenPartComponent<TestPartInterface> {

    ctorWasCalled: boolean;
    constructor( messageProvider: MessageProvider) {
        super( messageProvider );
        this.ctorWasCalled = true;
    }
    screenDataUpdated() {
    }
}

describe('ScreenPart', () => {
    let sut: TestPartComponent;
    let testScreen = {};
    function setup() {
        const messageProviderSpy = jasmine.createSpyObj('MessageProvider', ['getScopedMessages$', 'getAllMessages$']);
        messageProviderSpy.getScopedMessages$.and.returnValue(cold('---x|', {x: testScreen}));
        messageProviderSpy.getAllMessages$.and.returnValue(cold('---x|', {x: testScreen}));
        TestBed.configureTestingModule({
            declarations: [ TestPartComponent ],
            providers: [
                { provide: MessageProvider, useValue: messageProviderSpy },
            ]
        });
        const fixture = TestBed.createComponent(TestPartComponent);
        sut = fixture.componentInstance;
        fixture.detectChanges();
        getTestScheduler().flush();
    }

    describe('constructor', () => {

        it('should set screenPartName', () => {
            setup();
            expect(sut.screenPartName).toBe('TestPart');
        });
        it('should preserve and call the constructor', () => {
            setup();
            expect(sut.ctorWasCalled).toBeTruthy();
        });
    });

    describe('getMessageSubscription', () => {

        it('should get the part data from the TestPart object on the screen', () => {

            testScreen = {
                screenType: 'Test',
                TestPart: {
                    testProperty: 'Yay'
                }
            };
            setup();
            expect(sut.screenData.testProperty).toBe('Yay');
        });

        it('should get the part data off screen', () => {
            testScreen = {
                screenType: 'Test',
                testProperty: 'boo'
            };
            setup();
            expect(sut.screenData.testProperty).toBe('boo');
        });

        it('should ignore Loading Screens', () => {

            testScreen = {
                screenType: 'Loading',
                testProperty: 'boo'
            };
            setup();
            expect(sut.screenData).toBeFalsy();
        });
    });

});
