import { SessionService } from '../services/session.service';
import { HelpTextService } from './help-text.service';
import { TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';

describe('HelpTextService', () => {

    let sessionServiceSpy: jasmine.SpyObj<SessionService>;
    let helpTextService: HelpTextService;

    beforeEach(() => {
        const sessionSpy = jasmine.createSpyObj('SessionService', ['registerMessageHandler']);

        TestBed.configureTestingModule({
            providers: [
                { provide: SessionService, useValue: sessionSpy },
                HelpTextService
            ]
        });
        helpTextService = TestBed.get(HelpTextService);
        sessionServiceSpy = TestBed.get(SessionService);
    });

    describe('initialize', () => {
        it('initializes the service', (done: DoneFn) => {
            helpTextService.initialize();
            expect(sessionServiceSpy.registerMessageHandler).toHaveBeenCalled();
            helpTextService.isAvailable().subscribe(value => {
                expect(value).toBe(false);
            });
            done();
        });
    });

    describe('handle', () => {
        it('emits help text when it exists on message', (done: DoneFn) => {
            const message = { 
                helpText: {
                    text: 'help me'
                }
            };
            helpTextService.handle(message);
            helpTextService.getText().subscribe(v => {
                expect(v).toEqual('help me');
            });
            done();
        });

        it('emits new help text when it exists on message', (done: DoneFn) => {
            (helpTextService.getText() as BehaviorSubject<string>).next('old help');
            const message = { 
                helpText: {
                    text: 'help me'
                }
            };
            helpTextService.handle(message);
            helpTextService.getText().subscribe(v => {
                expect(v).toEqual('help me');
            });
            done();
        });

        it('does not change help text when type is NoOp', (done: DoneFn) => {
            (helpTextService.getText() as BehaviorSubject<string>).next('existing help');
            const noopMessage = {
                screenType: 'NoOp'
            }
            helpTextService.handle(noopMessage);
            helpTextService.getText().subscribe(v => {
                expect(v).toEqual('existing help');
            });
            done();
        });

        it('emits null when screen with no help text', (done: DoneFn) => {
            (helpTextService.getText() as BehaviorSubject<string>).next('existing help');
            const message = {
                screen: 'some screen'
            }
            helpTextService.handle(message);
            helpTextService.getText().subscribe(v => {
                expect(v).toBeNull();
            });
            done();
        });
    });
});
