import {
    animate,
    animateChild,
    animation,
    group,
    keyframes,
    query,
    style,
    transition,
    trigger
} from '@angular/animations';

export const gradientInnerGlowLeftSteps = [
    style({backgroundPosition: '0 50%', offset: 0}),
    style({backgroundPosition: '100% 50%', offset: 1})
];

// Angular doesn't support playing an animation in reverse,
// so we need to duplicate the previous animation and manually reverse it and adjust offsets
export const gradientInnerGlowRightSteps = [
    style({backgroundPosition: '100% 50%', offset: 0}),
    style({backgroundPosition: '0 50%', offset: 1})
];

export const gradientInnerGlowLeftAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(gradientInnerGlowLeftSteps))
], {
    params: {
        time: '3.5s',
        ease: 'ease'
    }
});

export const gradientInnerGlowRightAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(gradientInnerGlowRightSteps))
], {
    params: {
        time: '3.5s',
        ease: 'ease'
    }
});

export const gradientInnerGlowTrigger = trigger('gradientInnerGlow', [
    transition('* => *', [
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            gradientInnerGlowLeftAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ]),
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            gradientInnerGlowRightAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    ])
]);