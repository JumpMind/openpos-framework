# OpenposClientCoreLib

This is the parent project for OpenPos Client Core Lib that orchestrats the building and packaging of the core library as well as running the test suite.
Code for the library can be found in `projects\openpos-client-core-lib\src\lib`.

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 7.3.5.

## Development

High level development process:

* Run `npm install` from `openpos-client-libs`
* Run `npm run develop` from `openpos-client-libs`. This will concurrently run the angular build task and scss bundle task in watch mode and create a symbolic link from your build output to your local npm registry. If you want unit tests to also run use `npm run develop:test` instead.
* Next you'll need to link the core build output into your projects `node_modules`. This is done by running the `npm link @jumpmind/openpos-client-core-lib` command from your client project root. Some gradle develop scripts will detect the presence of openpos-client-core-libs link and do this step for you.
* From there just run `npm start` from your client and you should see that any file change you make from the core will trigger the core build and a build of you client.

For debugging you will want to expose vendor source maps by adding this build option do `angular.json`

```json
    "sourceMap": {
        "hidden": true,
        "scripts": true,
        "styles": true,
        "vendor": true
        }
```

_**Note** You may see it trigger a few builds due to the multistep build process on the core to support various bundle types._

### Core Module

Put all the application wide, single use components and singleton services here. The core module should only be included once in an app, ideally from AppModule.

See the Angular Style Guide on [CoreModule](https://angular.io/guide/styleguide#core-feature-module) for more information.

### Shared Module

All components/pipes/directices that are used by components across multiple modules go here.

Any components that are dynamically created and therefore require to be EntryComponents should not be included in the SharedModule, use CoreModule instead.

See the Angular Style Guide on [SharedModule](https://angular.io/guide/styleguide#shared-feature-module) for more information.

#### Directives

TODO Need to add documentation for all of our directives

#### Pipes

TODO Need to add documentation for all of our pipes

#### Formatters

TODO

#### Validators

TODO

### Screens Module (Deprecated)

Components that are dynamically created by screen outlet directive and correspond to a Screen object on the server side.

These screens need to be put in both the EntryComponents and Declarations array of the module and added to the ScreenConstants file with a name that matches the Screen Object on the server side.

This screens module is deprecated in favor of adding screens to the ScreensWithParts module using the ScreenParts pattern.

### Temporarily Shared Screens Module

This module serves as a temporary bridge for screens that are used in both the ScreenModule and ScreensWithPartsModule. These screens will soon be converted to the ScreenParts pattern and removed from this module.

### Screens with Parts Module

Components that are dynamically created by screen outlet directive and correspond to a UIMessage object on the server side.
These screen should all follow the screen parts pattern, in this pattern there are 4 types of components.

#### Layout Components

These components help define structure and basic functionality that is shared across many screens, but know nothing about the data from the server. These components are 100% purely UI behavior. A basic example is the SideNav component, it defines 2 regions on the screen and accepts any content into them using content projection. It also has some basic behavior for how the drawer should operate in a mobile view vs. desktop view.

It is worth noting that you are not required to have a layout component on your screen. For example if you just have a simple header and a body with some text you gain nothing with a layout component.

#### Components (for lack of a better name)

These components define re-usable peices of UI logic, but again know nothing about the data from the server. They are also 100% purely UI behavior. They are really very similar to Layout Components, but seperate purely for organization. Examples of these are Buttons, TabControl, a list. They are more focused on how the component looks and operates and again know nothing about the data they will be presenting.

#### Screen Parts

Screen Parts provide a way to create a reusable component that presents the same type of data in the same way on multiple screens. Screen parts know about the data coming from the server and determine how that data is presented to the user and can send actions back to server. An example of this would be the SausageLinks, it expects a list of ActionItems and presents them in a vertical list of buttons styled in a particular way using other Components.

Screen Parts should extend ScreenPartComponent and pass in a generic argument of an interface the defines the data it expects. Optionally you can apply the @ScreenPart decorator to define a sub-object on the UIMessage to get its data. Otherwise it'll just cherry pick from the root UIMessage.

It is worth nothing that you are not required to use Screen Parts. Your screen might be simple enough or have nothing shared with other screens and then it doesn't gain you anything.

#### Screen

This is the component that brings it all together. A screen should have a 1 to 1 relationship with a UIMessage from the server and should composite all the other types of components together to create the final screen the user sees.

A Screen should extend PosScreen and pass in a generic argument of an interface that defines the data the screen needs. (This could be just a subset of the data since some of the screen may be rendered by ScreenParts)

A screen needs to be added to both the EntryComponents and Declarations sections of the ScreenWithPartsModule and have the @Screen decorator applied to it to define the name that maps to the UIMessage. The decorator register this component with the ScreenService and adds it to the factory used to determine which screen to create per UIMessage received.

#### Dialog

Same concept as a Screen but created in a dialog container instead of in the main document.

These also should also extend PosScreen but should have the @Dialog decorator applied instead of @Screen

### Keyboard Module

This feature module provides an onscreen keyboard for all inputs. To use this feature just import it into your AppModule.

### Self Checkout Module

This feature module provides screens specific to self checkout.

### Theming

TODO explain how theming works

## Build

There are 2 components to building this library, building and minifying the angular files and bundling the SASS theme files into a single flat scss file. This normally will be done by the build server but can be done manually by running `npm run build`. This will execute `ng build openpos-client-core-lib` followed by `scss-bundle -c projects/openpos-client-core-lib/scss-bundle.config.json`. This does a production build that places the build artifacts in `/dist/openpos-client-core-lib`

## Publishing

Publishing of the library is done by build server and is triggered by incrementing the version number in `projects\openpos-client-core-lib\package.json`. Once the updated package.json file is pushed to master the build server will build and publish the updated library to the NPM Registry.
