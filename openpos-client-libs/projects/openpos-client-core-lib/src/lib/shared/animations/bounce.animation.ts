import { animation, keyframes, style, animate } from '@angular/animations';

export const bounceAnimation = animation(
        animate('{{ time }} ease',
        keyframes([
            style({ transform: 'scale(1,1)      translateY(0)', offset: 0 }),
            style({ transform: 'scale(1.1,.9)   translateY(0)', offset: .1 }),
            style({ transform: 'scale(.9,1.1)   translateY(-{{ height }})', offset: .3 }),
            style({ transform: 'scale(1.05,.95) translateY(0)', offset: .5 }),
            style({ transform: 'scale(1,1)      translateY(-7px)', offset: .57 }),
            style({ transform: 'scale(1,1)      translateY(0)', offset: .64 }),
            style({ transform: 'scale(1,1)      translateY(0)', offset: 1})]
        )));

