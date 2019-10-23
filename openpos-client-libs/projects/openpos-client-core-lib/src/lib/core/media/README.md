# Responsive Media Types

OpenPOS Framework uses the Angular Material media breakpoints abstracted into mobile-portrait, tablet-portrait, desktop-portrait, mobile-landscape, tablet-landscape, and desktop-landscape. The OpenPOS Media Service utilizes the [Angular CDK BreakpointObserver](https://material.angular.io/cdk/layout/overview) to determine media sizing. These device sizes are then used consistently across the app to trigger, styles, layout and features.

Breakpoints can be overriden with the following client config in application.yml:

```yml
clientConfiguration:
  clientConfigSets:
  - tags: default
    configsForTags:
      MediaService:
        breakpoints:
          'mobile-portrait': '(max-width: 599.99px) and (orientation: portrait)'
          'mobile-landscape': '(max-width: 959.99px) and (orientation: landscape)'
          'tablet-portrait': '(min-width: 600px) and (max-width: 839.99px) and (orientation: portrait)'
          'tablet-landscape': '(min-width: 960px) and (max-width: 1279.99px) and (orientation: landscape)'
          'desktop-portrait': '(min-width: 840px) and (orientation: portrait)'
          'desktop-landscape': '(min-width: 1280px) and (orientation: landscape)'
```

The directive `responsive-class` when attached to an element will add and remove classes for the active device size so that classes can be built as such:

```scss
.element {
    // Default style
    &.mobile {
        // Style to apply when mobile target is active
    }
    &.tablet {
        // Style to apply when tablet target is active
    }
    &.mobile-portrait {
        // Style to apply when mobile target is active and in portrait orientation
    }
    &.tablet-landscape {
        // Style to apply when tablet target is active and in landscape orientation
    }
}
```

There is also an `opGridAreas` directive to replace `gdAreas` and work with the OpenPos responsive API: `opAreas.mobile, opAreas.tablet`
