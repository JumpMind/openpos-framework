# Personalization

Personalization is the process of specifying parameters that configure the functionality and appearance of the OpenPOS Client, such as Device ID, Server Name/Port, Brand ID, etc.  This must be done before the client app can communicate with the OpenPOS server.  Personalization can be done automatically via a startup task or manually through the personalization component.
As part of the personalization process your device will be given a token to use when subscribing to the server. This token make sure no other clients can impersonate your device and is also used to retrieve your device personalization on subsequent connections.

## Default Personalization Startup Task

This task runs during the startup process and will attempt to personalize your device in 3 different ways. 
- First it will check for query parameters to build the personalization request.
- If that fails it will try and reload a saved personalization using a stored deviceToken.
- Lastly it will prompt the user for manual personalization using the personalization component

## Personalization Component

This component prompts for user input for personalization parameters.  This process is done in 4-5 steps.  

The first step is optional, and prompts for the OpenPOS Client hostname/IP address, port, SSL, and app name/route.  This step is only shown if the ClientUrlService.navigateExternal property is true.  This would be used in the case where the personalization component is embedded in a separate app, such as in a Cordova or React Native app.

The second step prompts for the OpenPOS Server hostname/IP address, port, and SSL. In this step, either the host/port/ssl of an OpenPOS Server or the host/port/ssl of an OpenPOS Management Server can be provided.  In the case of an OpenPOS Server, this is used to communicate with the server to determine the remaining personalization parameters.  In the case of an OpenPOS Management Server, the host/port/ssl settings are used to contact the OpenPOS Management Server in order to first obtain the Device ID formatting meta data so that the Device ID can be validated in the next step.

The third step for both OpenPOS Server and OpenPOS Management Server installations is to prompt for the Device ID. The Device ID is validated against the regular expression defined by the devicePattern in the server configuration. The default devicePattern is `d{5}-\d{3}`.

In the case of an OpenPOS Management Server installation only, the fourth step is to 'discover' the targeted OpenPOS Server for the device associated with the given Device ID. The OpenPOS Management Server will either configure and launch OR immediately contact the OpenPOS Server for the device if it is already running.  In the case where initial configuration hasn't been done yet OR if the OpenPOS Server for the device isn't running, there will be a delay in a response from the OpenPOS Management Server until configuration and/or launching of the device's OpenPOS Server is completed. This could take as much as 60-90 seconds for some installations.  Once the OpenPOS Server for the device is running, a message is displayed in the discovery step that displays the host and port on which the device's OpenPOS Server is running. Also it is worth noting that the OpenPOS Management Server is used as a proxy during all connect/reconnects from the client to the server in order to properly resolve the connection parameters required for the client to connect to its corresponding OpenPOS Server instance.

For both the OpenPOS Server and OpenPOS Management Server installations, the final step prompts for any optionally configured personalization parameters (e.g. Brand ID, Device Type, etc.).  These parameters are configured in the OpenPOS Server's application.yml.  An example configuration below configures 2 additional personalization parmaters (Brand ID and Device Type) as well as configures a regular expression for validation of the Device ID (devicePattern).

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

This service manages the personalization process and stores server location and device token in the browser local storage.

The personalization service also manages the requesting of personalization parameters from the server (PersonalizationService.requestPersonalization).

## Client Url Service

This service manages the first step of personalization as well as navigation to the client url generated through personalization.


