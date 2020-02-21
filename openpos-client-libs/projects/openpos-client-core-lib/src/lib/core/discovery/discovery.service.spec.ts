import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import { PersonalizationService } from '../personalization/personalization.service';
import { AppInjector } from '../app-injector';
import { Injector } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { DiscoveryService } from '../discovery/discovery.service';
import {BehaviorSubject, Observable, of} from 'rxjs';


describe('DiscoveryService', () => {

    let personalizationServiceSpy: jasmine.SpyObj<PersonalizationService>;
    let discoveryService: DiscoveryService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;

    let serverNameMock;
    let serverPortMock;
    let appIdMock;
    let deviceIdMock;
    let sslEnabledMock;
    let isManagedServerMock;
    let personalizatioPropertiesMock;

    beforeEach(() => {
        const personalizationSpy = jasmine.createSpyObj('PersonalizationService',
            ['getDeviceId$', 'getIsManagedServer$', 'getPersonalizationProperties$',
             'getSslEnabled$', 'getServerName$', 'getServerPort$', 'getAppId$']);
        const spyHttpClient = jasmine.createSpyObj('HttpClient', ['get']);

        TestBed.configureTestingModule({
            imports: [
                HttpClientModule
              ],
            providers: [
                { provide: PersonalizationService, useValue: personalizationSpy },
                { provide: HttpClient, useValue: spyHttpClient },

            ]
        });

        serverNameMock = new BehaviorSubject('localhost');
        serverPortMock = new BehaviorSubject('6140');
        appIdMock = new BehaviorSubject('pos');
        deviceIdMock = new BehaviorSubject('00000-000');
        sslEnabledMock = new BehaviorSubject(false);
        isManagedServerMock = new BehaviorSubject(false);
        personalizatioPropertiesMock = new BehaviorSubject(null);

        AppInjector.Instance = TestBed.get(Injector);
        personalizationServiceSpy = TestBed.get(PersonalizationService);
        personalizationServiceSpy.getServerName$.and.returnValue(serverNameMock);
        personalizationServiceSpy.getServerPort$.and.returnValue(serverPortMock);
        personalizationServiceSpy.getAppId$.and.returnValue(appIdMock);
        personalizationServiceSpy.getSslEnabled$.and.returnValue(sslEnabledMock);
        personalizationServiceSpy.getIsManagedServer$.and.returnValue(isManagedServerMock);
        personalizationServiceSpy.getPersonalizationProperties$.and.returnValue(personalizatioPropertiesMock);
        personalizationServiceSpy.getDeviceId$.and.returnValue(deviceIdMock);

        httpClientSpy = TestBed.get(HttpClient);
        discoveryService = TestBed.get(DiscoveryService);


    });

    describe('getServerBaseUrl', () => {

        it('updates to ServerName should update ServerBaseUrl', fakeAsync(() => {

            tick();
            expect(discoveryService.getServerBaseURL()).toBe('http://localhost:6140');

            serverNameMock.next('remotehost');

            tick();

            expect(discoveryService.getServerBaseURL()).toBe("http://remotehost:6140");
        }));

        it('updates to ServerPort should update ServerBaseUrl', fakeAsync(() => {

            tick();
            expect(discoveryService.getServerBaseURL()).toBe('http://localhost:6140');

            serverPortMock.next('6142');

            tick();

            expect(discoveryService.getServerBaseURL()).toBe("http://localhost:6142");
        }));

        it('updates to sslEnabled should update ServerBaseUrl', fakeAsync(() => {

            tick();
            expect(discoveryService.getServerBaseURL()).toBe('http://localhost:6140');

            sslEnabledMock.next(true);

            tick();

            expect(discoveryService.getServerBaseURL()).toBe("https://localhost:6140");
        }));


    });

 describe('getWebsocketUrl', () => {

     it('updates to ServerName should update WebSocketUrl', fakeAsync(() => {

         tick();
         expect(discoveryService.getWebsocketUrl()).toBe('ws://localhost:6140/api/websocket');

         serverNameMock.next('remotehost');

         tick();

         expect(discoveryService.getWebsocketUrl()).toBe('ws://remotehost:6140/api/websocket');
     }));

     it('updates to ServerPort should update WebSocketUrl', fakeAsync(() => {

         tick();
         expect(discoveryService.getWebsocketUrl()).toBe('ws://localhost:6140/api/websocket');

         serverPortMock.next('6142');

         tick();

         expect(discoveryService.getWebsocketUrl()).toBe('ws://localhost:6142/api/websocket');
     }));

     it('updates to sslEnabled should update WebSocketUrl', fakeAsync(() => {

         tick();
         expect(discoveryService.getWebsocketUrl()).toBe('ws://localhost:6140/api/websocket');

         sslEnabledMock.next(true);

         tick();

         expect(discoveryService.getWebsocketUrl()).toBe('wss://localhost:6140/api/websocket');
     }));
    });

    describe('isManagementServerAlive', () => {

         it('should return false for non-managed server', async () => {
             expect(await discoveryService.isManagementServerAlive()).toBe(false);
         });

         it('should ping managed server', async () => {
             httpClientSpy.get.and.returnValue(of({success: true}));
             isManagedServerMock.next(true);
             await discoveryService.isManagementServerAlive();
             expect(httpClientSpy.get).toHaveBeenCalledWith('http://localhost:6140/ping', jasmine.anything());
         });

     });

    describe('discoverDeviceProcess', () => {
    beforeEach(() => {
        isManagedServerMock.next(true);
    });

    it('with no args should call discover web service on personalized server/port', async () => {
        discoveryService.discoverDeviceProcess();
        expect(httpClientSpy.get).toHaveBeenCalledWith('http://localhost:6140/discover?deviceId=00000-000', jasmine.anything());
    });

    it('with DiscoveryParams provided should call discover web service using those params', async () => {
        discoveryService.discoverDeviceProcess({
            server: 'foo',
            port: '1111',
            deviceId: '12345-6789',
            sslEnabled: true,
            appId: 'app'
        });
        expect(httpClientSpy.get).toHaveBeenCalledWith('https://foo:1111/discover?deviceId=12345-6789&appId=app', jasmine.anything());
    });

    it('secure urls are properly set and returned when getWebsocketUrl is called', async () => {
        httpClientSpy.get.and.returnValue(of({
            success: true,
            host: 'foo.local',
            port: '6140',
            webServiceBaseUrl: 'http://foo.local:6140',
            secureWebServiceBaseUrl: 'https://foo.local:6140',
            webSocketBaseUrl: 'ws://foo.local:6140',
            secureWebSocketBaseUrl: 'wss://foo.local:6140'
        }));
        const result = await discoveryService.discoverDeviceProcess({
            server: 'foo',
            port: '1111',
            deviceId: '12345-6789',
            sslEnabled: true,
            appId: 'app',
            maxWaitMillis: 5000
        });
        expect(httpClientSpy.get).toHaveBeenCalledWith('https://foo:1111/discover?deviceId=12345-6789&appId=app', jasmine.anything());
        expect(discoveryService.getWebsocketUrl()).toEqual('wss://foo.local:6140');
        expect(discoveryService.getServerBaseURL()).toEqual('https://foo.local:6140');
    });

    it('non ssl urls are properly set and returned when getWebsocketUrl is called', async () => {
        const httpClientResultSpy = httpClientSpy.get.and.returnValue(of({
            success: true,
            host: 'foo.local',
            port: '6140',
            webServiceBaseUrl: 'http://foo.local:6140',
            secureWebServiceBaseUrl: 'https://foo.local:6140',
            webSocketBaseUrl: 'ws://foo.local:6140',
            secureWebSocketBaseUrl: 'wss://foo.local:6140'
        }));
        const result = await discoveryService.discoverDeviceProcess({
            server: 'foo',
            port: '1111',
            deviceId: '12345-6789',
            sslEnabled: false,
            appId: 'app',
            maxWaitMillis: 5000
        });
        expect(httpClientSpy.get).toHaveBeenCalledWith('http://foo:1111/discover?deviceId=12345-6789&appId=app', jasmine.anything());
        expect(discoveryService.getWebsocketUrl()).toEqual('ws://foo.local:6140');
        expect(discoveryService.getServerBaseURL()).toEqual('http://foo.local:6140');
    });

});

});
