# Task List

This component is meant to be the container for `TaskCheckBoxComponent` and `TaskCheckAllBoxComponent` to create a tri-state check box that works with a list of items that have an TaskCheckBoxComponent element.

```html
<div app-task-list>
    <app-task-check-all-box></app-task-check-all-box>
    
    <div *ngFor="let item of items">
        <span>{{item.description}}</span>
        <app-task-check-box [(checked)]="checked"></app-task-check-box>
    </div>

</div>
```