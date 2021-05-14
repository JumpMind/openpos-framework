import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'app-status-details-item',
    templateUrl: 'status-details-item.component.html',
    styleUrls: ['status-details-item.component.scss']
})

export class StatusDetailsItemComponent {
    @Input()
    status: 'online' | 'offline' | 'error';

    @Input()
    icon?: string;

    @Input()
    title: string;

    @Input()
    actionText?: string;

    @Input()
    titleTransparency: string;

    @Output()
    actionExecuted = new EventEmitter<void>();

    @Input()
    enabled: boolean;
}