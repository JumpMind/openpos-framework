import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { Plugins } from '@capacitor/core';

@Component({
    selector: 'app-scandit-native',
    templateUrl: 'scandit-native.component.html'
})
export class ScanditNativeComponent implements OnInit, OnDestroy {
    constructor(private scannerHost: ElementRef<HTMLElement>) { }

    ngOnInit() {
        Plugins.ScanditNative.initialize({
            apiKey: 'ARN/oCgVCGzbEFujPh+FQ94sXhF6A9ok9kmmdG1v11V7S7ipXlnPqf9NBYJkEwim7XsZSgR0RtadNhGtFGyktsMDKtBJJh4Gahfn1+1BTOFlUsmiSCbIulsm2zRNM7yZhA6UidAJNveJpFPpngIwaGZbMPtwYcyW+tyKpD+WGbjWnHhPGl0iufLv/e1byT+wG5y6e9NkfthlCyJ8GyJ79NB6lxRLEkEOb0kSMMTABvPe464/TjBY+/K14p+kG0NEzAcYNWVX2HIzIccJxdEKXnqmWwWYulTTPteMDpJIKUZQK+iqkeifssj/HblPye8yYuoWzasFnL47Q1T5hOg0+LXSZ1HbSqV+/9WcRc2s6m/qzPQfnyAmH75gypHXsGfX/VVPQbLorkfqGmpbH0zP7RXa+UdnGU0DsOpH/WjFNA9k3w6xT9PavaQ4MQ4yA7MaTN438FbBultRUxuQJ7mYeTbGZZtNkUVqYtM/0pJB0xzS7MjGmxBnO/HElYtvGgN4V/J8lmfsoS3tZOCMzc9ePgCc4pwRDbaYWAEUID529JKp7YAkhdX93d8Vu+xjbspUBWcQx26gGms0m6FiV9humB+dnnGR4DIG4o0RvVpPk18tkhM2Ki+CeNHwNdf/2gkMVQLIlUDIbBeiaAXaG77w/odNQo/GKQvNzq3v56DZDBPUWifGBQD7sCuKQccBb+1XbLB0sQT2V2tg2PWLYb4WLzl2wapUfr8tzm8MBLI1jMGnTJVqoZVjI/f3yC5siOO1HnxE28/xlkqd3VE/IGINfelacaDKTlJgheAl0fiU9qol/LM='
        });

        Plugins.ScanditNative.addView()

        setTimeout(this.updateView.bind(this), 100);

        let element: HTMLElement = this.scannerHost.nativeElement;
        while (element != document.body) {
            element = this.getScrollParent(element, true);
            element.addEventListener('scroll', this.updateView.bind(this));
        }

        const scroller = this.getScrollParent(this.scannerHost.nativeElement, true);

        scroller.addEventListener('scroll', e => {
            this.updateView();
        });

        window.addEventListener('orientationchange', e => {
            this.updateView();

            setTimeout(this.updateView.bind(this), 100);
            setTimeout(this.updateView.bind(this), 300);
            setTimeout(this.updateView.bind(this), 1000);
        });
    }

    async ngOnDestroy() {
        await Plugins.ScanditNative.removeView();
    }

    private getScrollParent(element: HTMLElement, includeHidden: boolean): HTMLElement {
        var style = getComputedStyle(element);
        var excludeStaticParent = style.position === "absolute";
        var overflowRegex = includeHidden ? /(auto|scroll|hidden)/ : /(auto|scroll)/;
    
        if (style.position === "fixed") return document.body;
        for (var parent = element; (parent = parent.parentElement);) {
            style = getComputedStyle(parent);
            if (excludeStaticParent && style.position === "static") {
                continue;
            }
            if (overflowRegex.test(style.overflow + style.overflowY + style.overflowX)) return parent;
        }
    
        return document.body;
    }

    private async updateView(): Promise<void> {
        const rect = this.scannerHost.nativeElement.getBoundingClientRect();

        console.log("UPDATING VIEW", rect);

        await Plugins.ScanditNative.updateView({
            top: rect.top,
            left: rect.left,
            width: rect.width,
            height: rect.height
        });
    }
}