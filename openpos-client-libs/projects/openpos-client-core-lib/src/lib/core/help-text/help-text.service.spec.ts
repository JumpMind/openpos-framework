import { SessionService } from '../services/session.service';
import { HelpTextService } from './help-text.service';
import { TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { HelpText } from '../interfaces/help-text.interface';

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
                    text: 'help me',
                    title: 'title'
                }
            };
            helpTextService.handle(message);
            helpTextService.getHelpText().subscribe(v => {
                expect(v.text).toEqual('help me');
                expect(v.title).toEqual('title');
            });
            done();
        });

        it('emits new help text when it exists on message', (done: DoneFn) => {
            const oldHelpText: HelpText = {
                    id: 'id',
                    text: 'old help',
                    title: 'old title'
            };
            (helpTextService.getHelpText() as BehaviorSubject<HelpText>).next(oldHelpText);
            const message = { 
                helpText: {
                    text: 'help me',
                    title: 'new title'
                }
            };
            helpTextService.handle(message);
            helpTextService.getHelpText().subscribe(v => {
                expect(v.text).toEqual('help me');
                expect(v.title).toEqual('new title');
            });
            done();
        });

        it('does not change help text when type is NoOp', (done: DoneFn) => {
            const oldHelpText: HelpText = {
                id: 'id',
                text: 'existing help',
                title: 'existing title'
            };
            (helpTextService.getHelpText() as BehaviorSubject<HelpText>).next(oldHelpText);
            const noopMessage = {
                screenType: 'NoOp'
            }
            helpTextService.handle(noopMessage);
            helpTextService.getHelpText().subscribe(v => {
                expect(v.text).toEqual('existing help');
                expect(v.title).toEqual('existing title');
            });
            done();
        });

        it('emits null when screen with no help text', (done: DoneFn) => {
            const oldHelpText: HelpText = {
                id: 'id',
                text: 'existing help',
                title: 'existing title'
            };
            (helpTextService.getHelpText() as BehaviorSubject<HelpText>).next(oldHelpText);
            const message = {
                screen: 'some screen'
            }
            helpTextService.handle(message);
            helpTextService.getHelpText().subscribe(v => {
                expect(v).toBeNull();
            });
            done();
        });
    });
});
