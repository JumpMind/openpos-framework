import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class AnimationService {
    private renderer: Renderer2;

    constructor(private rendererFactory: RendererFactory2) {
        // Angular doesn't let you inject Render2 directly into a service, so we need to get around it this way
        this.renderer = rendererFactory.createRenderer(null, null);
    }

    public restart(selector: string | Element, animationClass: string): void {
        if (!selector) {
            return;
        }

        let element = typeof selector === 'string' ? document.querySelector(selector) : selector;

        if (element) {
            this.renderer.removeClass(element, animationClass);
            // A trick to trigger a browser browser reflow layout calculations, which will cause animations to run
            // again like it's the first time
            element.getBoundingClientRect();
            this.renderer.addClass(element, animationClass);
        }
    }
}


