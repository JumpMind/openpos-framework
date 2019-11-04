import { TestBed } from '@angular/core/testing';
import { PersonalizationService } from '../personalization/personalization.service';
import { AppInjector } from '../app-injector';
import { Injector } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { DiscoveryService } from '../discovery/discovery.service';
import { Observable, of } from 'rxjs';


describe('SessionService', () => {

    let personalizationServiceSpy: jasmine.SpyObj<PersonalizationService>;
    let discoveryService: DiscoveryService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;

    beforeEach(() => {
        const personalizationSpy = jasmine.createSpyObj('PersonalizationService',
            ['getDeviceId', 'isManagedServer', 'getPersonalizationProperties', 'isSslEnabled', 'getServerName', 'getServerPort']);
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

        AppInjector.Instance = TestBed.get(Injector);
        personalizationServiceSpy = TestBed.get(PersonalizationService);
        httpClientSpy = TestBed.get(HttpClient);
        discoveryService = TestBed.get(DiscoveryService);

        personalizationServiceSpy.getServerName.and.returnValue('server');
        personalizationServiceSpy.getServerPort.and.returnValue(6140);
        personalizationServiceSpy.isSslEnabled.and.returnValue(false);
        personalizationServiceSpy.isManagedServer.and.returnValue(false);
        personalizationServiceSpy.getPersonalizationProperties.and.returnValue(null);
        personalizationServiceSpy.getDeviceId.and.returnValue('00000-000');
    });

    describe('clearCachedUrls', () => {
        beforeEach(() => {
        });

        it('should leave urls empty', () => {
            discoveryService.clearCachedUrls();
            discoveryService.getServerBaseURL();

            expect(personalizationServiceSpy.getServerName).toHaveBeenCalled();
        });
    });

    describe('repeated calls to get urls', () => {
        beforeEach(() => {
        });

        it('should only result in one call to personalization to get the server name for server base url', () => {
            discoveryService.getServerBaseURL();
            discoveryService.getServerBaseURL();
            expect(personalizationServiceSpy.getServerName).toHaveBeenCalledTimes(1);
        });

        it('should only result in one call to personalization to get the server name for websocket url', () => {
            discoveryService.getWebsocketUrl();
            discoveryService.getWebsocketUrl();
            expect(personalizationServiceSpy.getServerName).toHaveBeenCalledTimes(1);
        });

    });

    describe('isManagementServerAlive', () => {
        beforeEach(() => {
        });

        it('should return false for non-managed server', async () => {
            expect(await discoveryService.isManagementServerAlive()).toBe(false);
        });

        it('should ping managed server', async () => {
            httpClientSpy.get.and.returnValue(of({success: true}));
            personalizationServiceSpy.isManagedServer.and.returnValue(true);
            discoveryService.isManagementServerAlive();
            expect(httpClientSpy.get).toHaveBeenCalledWith('http://server:6140/ping', jasmine.anything());
        });

    });

    describe('discoverDeviceProcess', () => {
        beforeEach(() => {
            personalizationServiceSpy.isManagedServer.and.returnValue(true);
        });

        it('with no args should call discover web service on personalized server/port', async () => {
            discoveryService.discoverDeviceProcess();
            expect(httpClientSpy.get).toHaveBeenCalledWith('http://server:6140/discover?deviceId=00000-000', jasmine.anything());
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
