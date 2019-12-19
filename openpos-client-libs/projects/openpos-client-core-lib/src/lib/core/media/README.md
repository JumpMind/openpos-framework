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

## Media Sizing

### Screen Sizes

OpenPOS screens have been designed to support a wide range of screen resolutions and aspect ratios.  The majority of screens use media breakpoints that service desktop, tablet, and mobile resolutions as well as portrait vs landscape orientation.

Common screen sizing for desktop applications is 1920x1080px. Self checkout screens are designed to better match portrait orientation screen resolutions, such as 1080x1920px. Tablet screens support resolutions between 960x600px and 840x1280px in either orientation. Mobile screens support resolutions up to 600x960px in portrait orientation.

### Icons

Icons are served as SVG files, so they can be resized on the client without loss of quality.  The majority of icons are square shaped, with typical sizing of 24x24px, 36x36px, 48x48px, and 64x64px.

### Images

Images vary in size and shape but are typically limited to 50% of the screen height and width. Most images are designed based on the particular orientation and spacing of the screen (i.e. if the image container or screen is a portrait aspect ratio, so is the image).
