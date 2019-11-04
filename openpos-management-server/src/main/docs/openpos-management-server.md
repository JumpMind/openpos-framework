---
id: openpos-management-server
title: OpenPOS Management Server
---

# Overview
The OpenPOS Management Server supports managing of multiple OpenPOS server instances
for situations that are running with POS legacy deployments and require device/register process isolation.

If an OpenPOS Management Server host/port is specified during the Personalization process on the client,
the OpenPOS client will dynamically adjust and attempt 'discovery' of its client OpenPOS Server process
through the OpenPOS Management Server. If required, the OpenPOS Management Server will configure, install,
and launch an OpenPOS Server process for the device. Once the device process is running, 
the OpenPOS Management Server will return URL connection info to the client and the 
client will use the URL returned to make a direct websocket connection to the OpenPOS Server process.
The Discovery Service and Session Service on the client also includes requisite logic 
to re-establish broken connections from the client. On the server side, there is logic 
to auto launch (or re-launch) any OpenPOS Server processes that are not running or have for some reason crashed.