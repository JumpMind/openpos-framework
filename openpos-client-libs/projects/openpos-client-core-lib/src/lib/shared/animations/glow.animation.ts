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

export const glowExpandSteps = [
    style({filter: 'drop-shadow(0 0 0 currentColor)', offset: 0}),
    style({filter: 'drop-shadow(0 0 0 currentColor)', offset: .15}),
    style({filter: 'drop-shadow(0 0 5px currentColor) drop-shadow(0 0 20px currentColor)', offset: 1})
];

// Angular doesn't support playing an animation in reverse,
// so we need to duplicate the previous animation and manually reverse it and adjust offsets
export const glowContractSteps = [
    style({filter: 'drop-shadow(0 0 5px currentColor) drop-shadow(0 0 20px currentColor)', offset: 0}),
    style({filter: 'drop-shadow(0 0 0 currentColor)', offset: .85}),
    style({filter: 'drop-shadow(0 0 0 currentColor)', offset: 1})
];

export const glowExpandAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(glowExpandSteps))
], {
    params: {
        time: '1.23s',
        ease: 'ease-out'
    }
});

export const glowContractAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(glowContractSteps))
], {
    params: {
        time: '1.23s',
        ease: 'ease-out'
    }
});

export const glowExpandTrigger = trigger('glowExpand', [
    transition('* => *',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            glowExpandAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    )
]);

export const glowContractTrigger = trigger('glowContract', [
    transition('* => *',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            glowContractAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    )
]);

export const glowPulseTrigger = trigger('glowPulse', [
    transition('true => false',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            glowExpandAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    ),
    transition('false => true',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            glowContractAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    )
]);