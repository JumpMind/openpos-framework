package org.jumpmind.pos.server.service;

import org.jumpmind.pos.util.model.Message;

public interface IIncidentService {
    Message createIncident(Throwable throwable, IncidentContext incidentContext);
}
