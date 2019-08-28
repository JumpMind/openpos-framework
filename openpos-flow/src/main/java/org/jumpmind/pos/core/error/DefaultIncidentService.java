package org.jumpmind.pos.core.error;

import org.jumpmind.pos.util.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

@Configurable
public class DefaultIncidentService implements IIncidentService {
    private static final Logger log = LoggerFactory.getLogger(DefaultIncidentService.class);
​
    private static final String INCIDENT_ID_FILENAME = ".incident_id";

    private File incidentIdFile;
    private Integer incidentCount;
    private String incidentId;

    @Override
    public Message createIncident(Throwable throwable, IncidentContext incidentContext) {

        return null;
    }

    private synchronized String generateIncidentId( String deviceId ) {
        int lastId = getLastIncidentCount();
        this.incidentCount = ++lastId;
        persistIncidentCount();

        this.incidentId = String.format("%s-%d", deviceId, this.incidentCount);
        return this.incidentId;
    }

    private void persistIncidentCount() {
        try(
                PrintWriter printWriter = new PrintWriter(incidentIdFile);
        ) {
            printWriter.print(this.incidentCount != null ? this.incidentCount : 0);
        } catch (IOException e) {
            log.warn(String.format("Failed to create '%s' file. Reason: %s", INCIDENT_ID_FILENAME, e.getMessage()), e);
        }

    }
    private void ensureIncidentIdFileExists() {
        if (null == incidentIdFile) {
            incidentIdFile = new File(StoreProperties.getInstance().getWorkingDir(), INCIDENT_ID_FILENAME);
        }

        if (! incidentIdFile.exists()) {
            persistIncidentCount();
        }
    }

    private int getLastIncidentCount() {
        ensureIncidentIdFileExists();
        if (this.incidentCount == null) {
            try (
                    Scanner scanner = new Scanner(incidentIdFile).useDelimiter("\\Z");
            ) {
                String lastIdStr = scanner.next();
                this.incidentCount = Integer.valueOf(lastIdStr);
            } catch (FileNotFoundException | NumberFormatException ex) {
                this.incidentCount = 0;
            }
        }

        return this.incidentCount;
    }
​
}
