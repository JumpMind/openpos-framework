import {Component, Input, OnInit} from '@angular/core';
import {Plan} from "../../../screens-with-parts/sale/program-interface";
import {DomSanitizer} from "@angular/platform-browser";
import {SafeHtml} from "@angular/platform-browser/src/security/dom_sanitization_service";

@Component({
    selector: 'app-program-plan-details',
    templateUrl: './program-plan-details.component.html',
    styleUrls: ['./program-plan-details.component.scss']})
export class ProgramPlanDetailsComponent implements OnInit {
    @Input()
    programCopy: string;
    safeProgramCopy: SafeHtml;
    @Input()
    plans: Plan[];

    constructor(private sanitizer: DomSanitizer){}

    ngOnInit(): void {
        this.safeProgramCopy = this.sanitizer.bypassSecurityTrustHtml(this.programCopy);
    }
}
