// Angular Includes
import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { PriceCheckerItemDetailComponent } from './price-checker-item-detail/price-checker-item-detail.component';
import { PriceCheckerHomeComponent } from './price-checker-home/price-checker-home.component';

const screens = [
    PriceCheckerItemDetailComponent,
    PriceCheckerHomeComponent
];

@NgModule({
    entryComponents: [
        ...screens
    ],
    declarations: [
        ...screens
    ],
    imports: [
        SharedModule
    ]
})
export class PriceCheckerModule {}
