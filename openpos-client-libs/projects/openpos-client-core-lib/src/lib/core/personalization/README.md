# Personalization

Personalization is the process of specifying parameters that configure the functionality and appearance of the OpenPOS Client, such as Device ID, Server Name/Port, Brand ID, etc.  This must be done before the client app can communicate with the OpenPOS server.  Personalization can be done automatically via a startup task or manually through the personalization component.

## Personalization Component

This component prompts for user input for personalization parameters.  This process is done in 2-3 steps.  

The first step is optional, and prompts for the OpenPOS Client hostname/IP address, port, SSL, and app name/route.  This step is only shown if the ClientUrlService.navigateExternal property is true.  This would be used in the case where the personalization component is embedded in a separate app, such as in a Cordova or React Native app.

The second step prompts for the OpenPOS Server hostname/IP address, port, and SSL.  This is used to communicate with the server to determine the remaining personalization parameters.

The third and final step prompts for Device ID and any optionally configured personalization parameters (e.g. Brand ID, Device Type, etc.).  These parameters are configured in the server's application.yml.  An example configuration below configures 2 additional personalization parmaters (Brand ID and Device Type) as well as configures a regular expression for validation of the Device ID (devicePattern).

```
openpos:
    ui:
        personalization:
            devicePattern: '\d{5}-\d{3}'
            parameters:
            -
                property: 'brandId'
                label: 'Brand ID'
                defaultValue: 'default'
            -
                property: 'deviceType'
                label: 'Device Type'
                defaultValue: 'desktop'
```

## Personalization Service

This service manages the personalization process and stores personalization parameters in the browser local storage.

The personalization service also manages the requesting of personalization parameters from the server (PersonalizationService.requestPersonalization).

## Client Url Service

This service manages the first step of personalization as well as navigation to the client url generated through personalization.
