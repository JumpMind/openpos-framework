import { TestBed, ComponentFixture } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { AccentButtonComponent } from './accent-button.component';
import { By } from '@angular/platform-browser';


describe('AccentButtonComponent', () => {
    let component: AccentButtonComponent;
    let fixture: ComponentFixture<AccentButtonComponent>;

    beforeEach( () => {
        TestBed.configureTestingModule({
            imports: [],
            declarations: [ AccentButtonComponent ],
            providers: []
        }).compileComponents();
        fixture = TestBed.createComponent(AccentButtonComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('renders', () => {
        expect(component).toBeDefined();
    });

    describe('component', () => {
        it('does not call the buttonClick.emit when disabled is true', () => {
            component.disabled = true;
            spyOn(component.buttonClick, 'emit');
            component.clickFn();
            expect(component.buttonClick.emit).not.toHaveBeenCalled();
        });

        it('calls the buttonClick.emit when disabled is false', () => {
            component.disabled = false;
            spyOn(component.buttonClick, 'emit');
            component.clickFn();
            expect(component.buttonClick.emit).toHaveBeenCalledWith(true);
        });
    });

    describe('template', () => {
        let button;
        beforeEach(() => {
            button = fixture.debugElement.query(By.css('button'));
        });

        it('renders a button', () => {
            expect(button.nativeElement).toBeDefined();
        });

        describe('properties', () => {
            const validateAttribute = (element: DebugElement, name: string, value: any = '') => {
                expect(element.attributes[name]).toBe(value);
            };
            const validateProperty = (element: DebugElement, name: string, value: any = '') => {
                expect(element.properties[name]).toBe(value);
            };
            it('has the cdkFocusInitial directive', () => {
                validateAttribute(button, 'cdkFocusInitial');
            });
            it('has the responsive-class directive', () => {
                validateAttribute(button, 'responsive-class');
            });
            it('has the mat-flat-button directive', () => {
                validateAttribute(button, 'mat-flat-button');
            });
            it('binds the disabled property to the disabled directive', () => {
                component.disabled = true;
                fixture.detectChanges();
                button = fixture.debugElement.query(By.css('button'));
                validateProperty(button, 'disabled', component.disabled);
            });
            it('binds the type property to the type directive', () => {
                component.type = 'some type';
                fixture.detectChanges();
                button = fixture.debugElement.query(By.css('button'));
                validateProperty(button, 'type', component.type);
            });
            it('has a color property of "accent"', () => {
                validateAttribute(button, 'color', 'accent');
            });
        });

        describe('actions', () => {
            it('calls the components clickFn method when clicked', () => {
                spyOn(component, 'clickFn');
                button.nativeElement.click();
                expect(component.clickFn).toHaveBeenCalled();
            });
        });
    });
});