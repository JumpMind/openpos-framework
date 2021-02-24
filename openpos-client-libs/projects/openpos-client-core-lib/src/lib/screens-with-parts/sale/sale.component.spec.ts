import { TestBed, ComponentFixture } from '@angular/core/testing';
import { SaleComponent } from './sale.component';
import { MatDialog, MatBottomSheet } from '@angular/material';
import { OpenposMediaService } from '../../core/media/openpos-media.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { KeyPressProvider } from '../../shared/providers/keypress.provider';
import { NO_ERRORS_SCHEMA } from '@angular/core'
import { ActionService } from '../../core/actions/action.service';
import { ElectronService } from 'ngx-electron';
import { BaconStripComponent } from '../../shared/screen-parts/bacon-strip/bacon-strip.component';
import { ImageUrlPipe } from '../../shared/pipes/image-url.pipe';
import { TimeZoneContext } from '../../core/client-context/time-zone-context';
import { CLIENTCONTEXT } from '../../core/client-context/client-context-provider.interface';
import { SaleInterface } from './sale.interface';
import { Subscription, Observable, of } from 'rxjs';

class MockMatDialog {};
class MockActionService {};
class MockMatBottomSheet {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};
class MockElectronService {};
class ClientContext {};

describe('SaleComponent', () => {
    let component: SaleComponent;
    let fixture: ComponentFixture<SaleComponent>;
    let openposMediaSerivce: OpenposMediaService;

    describe('non mobile', () => {
        class MockOpenposMediaServiceMobile {
            observe(): Observable<boolean> {
                return of(false);
            }
        };
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule, ],
                declarations: [
                    SaleComponent, BaconStripComponent,
                    ImageUrlPipe
                ],
                providers: [
                    { provide: ActionService, useClass: MockActionService },
                    { provide: MatDialog, useClass: MockMatDialog},
                    { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobile },
                    { provide: MatBottomSheet, useClass: MockMatBottomSheet},
                    { provide: KeyPressProvider, useClass: MockKeyPressProvider },
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {}},
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(SaleComponent);
            component = fixture.componentInstance;
            component.screen = {} as SaleInterface;
            component.screen.orders = [];
            openposMediaSerivce = TestBed.get(OpenposMediaService);
            fixture.detectChanges();
        });

        it('renders', () => {
            expect(component).toBeDefined();
        });

        describe('component', () => {

        });

        describe('template', () => {

        });
    });
});