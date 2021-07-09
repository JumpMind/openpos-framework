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

export const shakeSteps = [
    style({transform: 'translate(1px, 1px) rotate(0deg)', offset: 0}),
    style({transform: 'translate(-1px, -2px) rotate(-1deg)', offset: .1}),
    style({transform: 'translate(-3px, 0px) rotate(1deg)', offset: .2}),
    style({transform: 'translate(3px, 2px) rotate(0deg)', offset: .3}),
    style({transform: 'translate(1px, -1px) rotate(1deg)', offset: .4}),
    style({transform: 'translate(-1px, 2px) rotate(-1deg)', offset: .5}),
    style({transform: 'translate(-3px, 1px) rotate(0deg)', offset: .6}),
    style({transform: 'translate(3px, 1px) rotate(-1deg)', offset: .7}),
    style({transform: 'translate(-1px, -1px) rotate(1deg)', offset: .8}),
    style({transform: 'translate(1px, 2px) rotate(0deg)', offset: .9}),
    style({transform: 'translate(1px, 1px) rotate(0deg)', offset: .95}),
    style({transform: 'translate(0, 0) rotate(0deg)', offset: 1})
];

export const shakeAnimation = animation([
    animate('{{ time }} {{ ease }}', keyframes(shakeSteps))
], {
    params: {
        time: '.75s',
        ease: 'ease-out'
    }
});

export const shakeTrigger = trigger('shake', [
    transition('* => *',
        // Run this and child animations in parallel to make it intuitive for developers to use
        group([
            shakeAnimation,
            // Angular blocks child animations while a parent's animations are running,
            // so they need to be manually triggered
            query('@*', animateChild(), {optional: true})
        ])
    )
]);