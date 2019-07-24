# Focus Service Overview

The focus service provides an interface to manage focus using [Angular Material CDK](https://material.angular.io/cdk/a11y/overview#activedescendantkeymanager).

This service is used by the Open POS framework to restore focus to the underlying screens when dialogs closed.

Clients implementing custom dialogs may need to use [FocusService](focus.service.ts) and call `restoreInitialFocus` after closing the dialog in order to 
an element on the screen.
