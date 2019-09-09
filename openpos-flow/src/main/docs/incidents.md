---
id: incidents
title: Incidents
---

Whenever an unexpected error occurs in the application it gets handed off to an implementation of an Incident service. 

## Default Incident Service

When an incident is created a unique id is associated with the incident and stored in a file `.incident_id`. The Incident is logged and a Toast Message is generated with the incident ID. The message and location of the incident id file are configurable.

~~~yaml
  incident:
    incidentIdFileLocation: 'work/'
    incidentMessage: 'An error has occurred. Please use incident id: %s when reporting the issue.'
~~~

## Overriding the Incident Service

If you need different behavior you can override the `IIncidentService` bean with your own implementation.
