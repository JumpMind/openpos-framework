import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';
import {PersonalizationConfigResponse} from './personalization-config-response.interface';
import {PersonalizationParameter} from './personalization-parameter.interface';
import {PersonalizationResponse} from './personalization-response.interface';
import {PersonalizationService} from './personalization.service';
import set = Reflect.set;

describe("PersonalizationService", () => {

    let httpMock: HttpTestingController;
    let personalizationService: PersonalizationService;


    function setup(){
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            providers: [PersonalizationService]
        });

        // Inject the http service and test controller for each test
        httpMock = TestBed.get(HttpTestingController);
        personalizationService = TestBed.get(PersonalizationService);
    }

    function cleanup(){
        localStorage.clear()
    }


    describe('personalizeFromSavedSession', () =>{

        it( 'should send personalization request with device token from local storage', () => {

            cleanup();

            localStorage.setItem('deviceToken', 'MY_TOKEN');
            localStorage.setItem('serverName', 'server');
            localStorage.setItem('serverPort', '6140');

            setup();

            personalizationService.personalizeFromSavedSession().subscribe( result => {
               expect(result).toBe('Personalization successful');
               expect(personalizationService.getDeviceId$().getValue()).toBe('11111-111');
               expect(personalizationService.getAppId$().getValue()).toBe('pos');
            });

            let req = httpMock.expectOne('http://server:6140/devices/personalize');

            expect(req.request.method).toBe('POST');

            let resp = {
                authToken: 'MY_TOKEN',
                deviceModel: {
                    deviceId: '11111-111',
                    appId: 'pos'
                }
            } as PersonalizationResponse;

            req.flush(resp)

            httpMock.verify();

        });

        it( 'should send personalization request with device token from local storage over ssl', () => {

            cleanup();

            localStorage.setItem('deviceToken', 'MY_TOKEN');
            localStorage.setItem('sslEnabled', 'true');
            localStorage.setItem('serverName', 'server');
            localStorage.setItem('serverPort', '6140');

            setup();

            personalizationService.personalizeFromSavedSession().subscribe( result => {
                expect(result).toBe('Personalization successful');
                expect(personalizationService.getDeviceId$().getValue()).toBe('11111-111');
                expect(personalizationService.getAppId$().getValue()).toBe('pos');
            });

            let req = httpMock.expectOne('https://server:6140/devices/personalize');

            expect(req.request.method).toBe('POST');

            let resp = {
                authToken: 'MY_TOKEN',
                deviceModel: {
                    deviceId: '11111-111',
                    appId: 'pos'
                }
            } as PersonalizationResponse;

            req.flush(resp)

            httpMock.verify();

        });

        it( 'should throw error if there is no cached token',() => {
            cleanup();
           setup();

           personalizationService.personalizeFromSavedSession().subscribe({
               error: error => expect(error).toEqual('No saved session')
           });

           httpMock.expectNone('http://server:6140/devices/personalize');

        });

    });

    describe( 'personalize', () => {
       it('should send personalization request to server and save response', () => {
            cleanup();
            setup();

            let params = new Map<string, string>();
            params.set('deviceType', 'register');

            personalizationService.personalize('server', '6140', '22222-222', 'pos', params, false).subscribe( result => {
                expect(result).toBe('Personalization successful');
                expect(personalizationService.getAppId$().getValue()).toBe('pos');
                expect(personalizationService.getDeviceId$().getValue()).toBe('22222-222');
                expect(personalizationService.getServerName$().getValue()).toBe('server');
                expect(personalizationService.getServerPort$().getValue()).toBe('6140');
                expect(personalizationService.getSslEnabled$().getValue()).toBe(false);
                expect(localStorage.getItem('deviceToken')).toBe('MY_TOKEN');
                expect(localStorage.getItem('serverName')).toBe('server');
                expect(localStorage.getItem('serverPort')).toBe('6140');
            });

            let req = httpMock.expectOne('http://server:6140/devices/personalize');

            let resp = {
               authToken: 'MY_TOKEN',
               deviceModel: {
                   deviceId: '22222-222',
                   appId: 'pos'
               }
            } as PersonalizationResponse;

            req.flush(resp);

            httpMock.verify();
       });

        it('should send personalization request to server and save response over ssl', () => {
            cleanup();
            setup();

            let params = new Map<string, string>();
            params.set('deviceType', 'register');

            personalizationService.personalize('server', '6140', '22222-222', 'pos', params, true).subscribe( result => {
                expect(result).toBe('Personalization successful');
                expect(personalizationService.getAppId$().getValue()).toBe('pos');
                expect(personalizationService.getDeviceId$().getValue()).toBe('22222-222');
                expect(personalizationService.getServerName$().getValue()).toBe('server');
                expect(personalizationService.getServerPort$().getValue()).toBe('6140');
                expect(personalizationService.getSslEnabled$().getValue()).toBe(true);
                expect(localStorage.getItem('deviceToken')).toBe('MY_TOKEN');
                expect(localStorage.getItem('serverName')).toBe('server');
                expect(localStorage.getItem('serverPort')).toBe('6140');
            });

            let req = httpMock.expectOne('https://server:6140/devices/personalize');

            let resp = {
                authToken: 'MY_TOKEN',
                deviceModel: {
                    deviceId: '22222-222',
                    appId: 'pos'
                }
            } as PersonalizationResponse;

            req.flush(resp);

            httpMock.verify();
        });
    });

    describe('dePersonalize', () => {

        it('should remove all values from local storage', () => {
            setup();

            personalizationService.dePersonalize();
            expect(localStorage.getItem('serverName')).toBeNull();
            expect(localStorage.getItem('serverPort')).toBeNull();
            expect(localStorage.getItem( 'deviceToken')).toBeNull();
            expect(localStorage.getItem('theme')).toBeNull();
            expect(localStorage.getItem('sslEnabled')).toBeNull();
        });

    });

    describe( 'requestPersonalizationConfig', () => {

        it('should request personalization config', () => {
            cleanup();
            setup();

            personalizationService.requestPersonalizationConfig('server', '6140', false).subscribe(result => {
                expect(result.devicePattern).toBe('pattern');
            });

            let req = httpMock.expectOne('http://server:6140/devices/personalizationConfig');

            expect(req.request.method).toBe('GET');

            let resp = {
                devicePattern: 'pattern',
                parameters: [
                    { label: 'Device Type', property: 'deviceType', defaultValue: 'default'} as PersonalizationParameter,
                ]
            } as PersonalizationConfigResponse;

            req.flush(resp);

            httpMock.verify();
        });

        it('should request personalization config over ssl', () => {
            cleanup();
            setup();

            personalizationService.requestPersonalizationConfig('server', '6140', true).subscribe(result => {
                expect(result.devicePattern).toBe('pattern');
            });

            let req = httpMock.expectOne('https://server:6140/devices/personalizationConfig');

            expect(req.request.method).toBe('GET');

            let resp = {
                devicePattern: 'pattern',
                parameters: [
                    { label: 'Device Type', property: 'deviceType', defaultValue: 'default'} as PersonalizationParameter,
                ]
            } as PersonalizationConfigResponse;

            req.flush(resp);

            httpMock.verify();
        });


    });
});