package org.jumpmind.pos.core.error;

import org.jumpmind.pos.util.model.Message;

public interface IIncidentService {
    Message createIncident(Throwable throwable, IncidentContext incidentContext);
}
