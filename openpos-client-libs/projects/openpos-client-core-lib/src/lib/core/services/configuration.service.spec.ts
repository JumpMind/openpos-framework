import { Configuration } from './../../configuration/configuration';
import { ConfigurationService } from './configuration.service';
import { SessionService } from './session.service';
import { TestBed } from '@angular/core/testing';
import { Logger } from './logger.service';
import { of } from 'rxjs';
import { cold, getTestScheduler } from 'jasmine-marbles';
import { ElectronService } from 'ngx-electron';

describe('ConfigurationService', () => {

    let configService: ConfigurationService;
    let sessionServiceSpy: jasmine.SpyObj<SessionService>;
    const testUiConfigMsgTemplate = {
        type : 'ConfigChanged',
        configType : 'uiConfig'
    };

    let testUiConfigMsg = testUiConfigMsgTemplate;

    function getUiConfigMessage() {
        return cold('-x', {x: testUiConfigMsg});
    }

    beforeEach(() => {
        testUiConfigMsg = JSON.parse(JSON.stringify(testUiConfigMsgTemplate));
        const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages']);
        TestBed.configureTestingModule({
            providers: [
                ElectronService,
                Logger,
                { provide: SessionService, useValue: sessionSpy },
            ]
        });
        sessionServiceSpy = TestBed.get(SessionService);
        sessionServiceSpy.getMessages.and.callFake(getUiConfigMessage);

        configService = TestBed.get(ConfigurationService);

    });

    it('test mapping of response string property to Configuration boolean property', () => {
        const originalValue = Configuration.mimicScroll;
        Configuration.mimicScroll = false;
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['mimicScroll'] = 'true';

        getTestScheduler().flush();
        expect(Configuration.mimicScroll).toEqual(true);
        Configuration.mimicScroll = originalValue;
    });

    it('test mapping of response boolean property to Configuration string property', () => {
        const originalValue = Configuration.compatibilityVersion;
        Configuration.compatibilityVersion = 'false';
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['compatibilityVersion'] = true;

        getTestScheduler().flush();
        expect(Configuration.compatibilityVersion).toEqual('true');
        Configuration.compatibilityVersion = originalValue;
    });

    it('test mapping of response string property to Configuration number property', () => {
        const originalValue = Configuration.keepAliveMillis;
        Configuration.keepAliveMillis = 1000;
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['keepAliveMillis'] = '1000';

        getTestScheduler().flush();
        expect(Configuration.keepAliveMillis).toEqual(1000);
        Configuration.keepAliveMillis = originalValue;
    });

    it('test mapping of response number property to Configuration string property', () => {
        const originalValue = Configuration.compatibilityVersion;
        Configuration.compatibilityVersion = '2';
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['compatibilityVersion'] = 1;

        getTestScheduler().flush();
        expect(Configuration.compatibilityVersion).toEqual('1');
        Configuration.compatibilityVersion = originalValue;
    });

    it('test mapping of response string property to Configuration string property', () => {
        const originalValue = Configuration.compatibilityVersion;
        Configuration.compatibilityVersion = '2';
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['compatibilityVersion'] = '1';

        getTestScheduler().flush();
        expect(Configuration.compatibilityVersion).toEqual('1');
        Configuration.compatibilityVersion = originalValue;
    });

    it('test mapping of invalid response string property to Configuration number property', () => {
        const originalValue = Configuration.mimicScroll;
        Configuration.mimicScroll = false;
        const propValueBeforeTest = Configuration.keepAliveMillis;
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['keepAliveMillis'] = 'foo';

        getTestScheduler().flush();
        expect(Configuration.keepAliveMillis).toEqual(propValueBeforeTest);
        Configuration.mimicScroll = originalValue;
    });

    it('test mapping of invalid response string property to Configuration boolean property', () => {
        const originalValue = Configuration.mimicScroll;
        Configuration.mimicScroll = false;
        const propValueBeforeTest = Configuration.mimicScroll;
        // tslint:disable-next-line:no-string-literal
        testUiConfigMsg['mimicScroll'] = 'foo';

        getTestScheduler().flush();
        expect(Configuration.mimicScroll).toEqual(propValueBeforeTest);
        Configuration.mimicScroll = originalValue;
    });
});
