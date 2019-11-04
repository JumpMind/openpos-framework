# Help Text

`<app-help-text-page-wrapper>` wraps the OpenPOS client application with on-screen help that can be access using the help button in the bacon strip.
Clients wanting this functionality can simply use the `<app-openpos-help-container-root>` instead of the `<app-openpos-root>` as the root of the application in their app.component.html.

Property | Description
---------|------------
`@Input() position = 'end'` | Key to specify the location of the help text. As default will be placed at the end. Other option is 'start'.

## Example Use
```html
    <app-dev-menu></app-dev-menu>
    <app-help-text-page-wrapper>
        <router-outlet></router-outlet>
    </app-help-text-page-wrapper>
```