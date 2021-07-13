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

export const throbSteps = [
    style({transform: 'scale(1)', offset: 0}),
    style({transform: 'scale(1.3)', offset: .5}),
    style({transform: 'scale(1)', offset: 1})
];

export const throbAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(throbSteps))
], {
    params: {
        time: '1.25s',
        ease: 'ease'
    }
});

export const throbTrigger = trigger('throb', [
    transition('* => *',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            throbAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    )
]);