import { Component, ViewChild, AfterViewInit, Renderer2, Input, OnDestroy, OnInit, ElementRef } from "@angular/core";
import { MatSidenavContainer } from "@angular/material";
import { Subscription } from "rxjs";
import { HelpTextService } from "../../../core/help-text/help-text.service";
import { ConfigurationService } from "../../../core/services/configuration.service";

@Component({
    selector: 'app-help-text-page-wrapper',
    templateUrl: './help-text-page-wrapper.component.html',
    styleUrls: ['./help-text-page-wrapper.component.scss']
})
export class HelpTextPageWrapperComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatSidenavContainer)
    sidenavContainer: MatSidenavContainer;

    @ViewChild('helpPageWrapperContainer')
    helpPageWrapperContainer: ElementRef;

    @Input()
    position = 'end';

    private currentTheme: string;

    private subscription = new Subscription();

    constructor(private helpTextService: HelpTextService, 
        private configurationService: ConfigurationService,
        private renderer: Renderer2) {
    }

    ngOnInit() {
        this.helpTextService.initialize();
    }

    ngAfterViewInit() {
        this.subscription.add(this.helpTextService.isSideNavViewable().subscribe(viewable => this.toggleSideNav(viewable)));
        this.subscription.add(this.configurationService.theme$.subscribe(theme => this.updateTheme(theme)));
    }

    private toggleSideNav(viewable: boolean) {
        if (viewable) {
            this.sidenavContainer.open();
        }
        else {
            this.sidenavContainer.close();
        }
    }

    private updateTheme(theme: string) {
        if (!!this.helpPageWrapperContainer) {
            if (!!this.currentTheme) {
                this.renderer.removeClass(this.helpPageWrapperContainer.nativeElement, this.currentTheme);
            }
            this.renderer.addClass(this.helpPageWrapperContainer.nativeElement, theme);
        }
        this.currentTheme = theme;
    }

    getText() {
        return this.helpTextService.getText();
    }

    close() {
        this.helpTextService.close();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    getDrawerMode() {
        return this.helpTextService.getDrawerMode();
    }
}