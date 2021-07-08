

// class MockActionService {};
// class MockMatDialog {};
// class MockElectronService {};
// class ClientContext {};
// class MockKeyPressProvider {
//     subscribe(): Subscription {
//         return new Subscription();
//     }
// };

describe('PlanDetailsDisplayComponent', () => {
    // let component: MembershipDisplayComponent;
    // let fixture: ComponentFixture<MembershipDisplayComponent>;
    // let membership: Membership;
    // beforeEach( () => {
    //     TestBed.configureTestingModule({
    //         imports: [ HttpClientTestingModule ],
    //         declarations: [
    //             MembershipDisplayComponent
    //         ],
    //         providers: [
    //             { provide: MatDialog, useClass: MockMatDialog },
    //             { provide: ActionService, useClass: MockActionService },
    //             { provide: ElectronService, useClass: MockElectronService },
    //             { provide: KeyPressProvider, useClass: MockKeyPressProvider },
    //             { provide: ClientContext, useValue: {}},
    //             { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
    //         ],
    //         schemas: [
    //             NO_ERRORS_SCHEMA,
    //         ]
    //     }).compileComponents();
    //     fixture = TestBed.createComponent(MembershipDisplayComponent);
    //     component = fixture.componentInstance;
    //     component.screenData = {
    //         memberIcon: 'check',
    //         nonMemberIcon: 'close'
    //     };
    //     membership = {
    //         id: '1',
    //         name: 'My Membership',
    //         member: true
    //     };
    //     component.membership = membership
    //     fixture.detectChanges();
    // });
    //
    // it('renders', () => {
    //     expect(component).toBeDefined();
    // });
    //
    // describe('component', () => {
    // });
    //
    // describe('template', () => {
    //     it('shows the name of the membership', () => {
    //         validateText(fixture, 'mat-chip', component.membership.name);
    //     });
    //
    //     it('calls clickEvent.emit when the chip is clicked', () => {
    //         spyOn(component.clickEvent, 'emit');
    //
    //         const chip = fixture.debugElement.query(By.css('mat-chip'));
    //         chip.nativeElement.click();
    //
    //         expect(component.clickEvent.emit).toHaveBeenCalledWith(component.membership);
    //     })
    //
    //     describe('when the user is a member', () => {
    //         beforeEach(() => {
    //             component.membership.member = true;
    //             fixture.detectChanges();
    //         });
    //
    //         it('has the "in" class', () => {
    //             const chip = fixture.debugElement.query(By.css('mat-chip'));
    //             expect(chip.nativeElement.classList).toContain('in');
    //         });
    //
    //         it('displays the membership icon', () => {
    //             validateIcon(fixture, 'mat-chip app-icon', 'check');
    //         });
    //
    //         it('does not have the non membership icon', () => {
    //             validateDoesNotExist(fixture, 'mat-chip app-icon .not-in');
    //         });
    //     });
    //
    //     describe('when the user is not a member', () => {
    //         beforeEach(() => {
    //             component.membership.member = false
    //             fixture.detectChanges();
    //         });
    //
    //         it('has the "not-in" class', () => {
    //             const chip = fixture.debugElement.query(By.css('mat-chip'));
    //             expect(chip.nativeElement.classList).toContain('not-in');
    //         });
    //
    //         it('does not have the membership icon', () => {
    //             validateDoesNotExist(fixture, 'mat-chip app-icon .in');
    //         });
    //
    //         it('has the non membership icon', () => {
    //             validateIcon(fixture, 'mat-chip app-icon', 'close');
    //         });
    //     });
    // });
});