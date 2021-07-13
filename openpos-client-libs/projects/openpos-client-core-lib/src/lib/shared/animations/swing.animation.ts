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

export const swingSteps = [
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: 0}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .08}),
    style({transform: 'rotate(42deg)', transformOrigin: '{{ origin }}', offset: .12}),
    style({transform: 'rotate(-35deg)', transformOrigin: '{{ origin }}', offset: .16}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .2}),
    style({transform: 'rotate(28deg)', transformOrigin: '{{ origin }}', offset: .23}),
    style({transform: 'rotate(-20deg)', transformOrigin: '{{ origin }}', offset: .26}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .29}),
    style({transform: 'rotate(16deg)', transformOrigin: '{{ origin }}', offset: .31}),
    style({transform: 'rotate(-12deg)', transformOrigin: '{{ origin }}', offset: .33}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .35}),
    style({transform: 'rotate(-6deg)', transformOrigin: '{{ origin }}', offset: .37}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .39}),
    style({transform: 'rotate(6deg)', transformOrigin: '{{ origin }}', offset: .4}),
    style({transform: 'rotate(-3deg)', transformOrigin: '{{ origin }}', offset: .44}),
    style({transform: 'rotate(2deg)', transformOrigin: '{{ origin }}', offset: .49}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .55}),
    style({transform: 'rotate(1deg)', transformOrigin: '{{ origin }}', offset: .62}),
    style({transform: 'rotate(0deg)', transformOrigin: '{{ origin }}', offset: .7})
];

export const swingAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(swingSteps))
], {
    params: {
        time: '3.5s',
        ease: 'ease',
        origin: 'center top'
    }
});

export const swingTrigger = trigger('swing', [
    transition('* => *',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            swingAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    )
]);