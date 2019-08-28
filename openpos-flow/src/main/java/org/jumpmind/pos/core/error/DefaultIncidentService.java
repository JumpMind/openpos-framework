package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.util.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

@ConfigurationProperties(prefix = "openpos.incident")
@Component
public class DefaultIncidentService implements IIncidentService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIncidentService.class);

    private static final String INCIDENT_ID_FILENAME = ".incident_id";

    private String incidentIdFileLocation;
    private String incidentMessage;

    private File incidentIdFile;
    private Integer incidentCount;
    private String incidentId;

    @Override
    public Message createIncident(Throwable throwable, IncidentContext incidentContext) {
        String incidentId = "unknown";
        try {
            incidentId = generateIncidentId(incidentContext.getDeviceId());
        } catch (Exception ex) {
            logger.warn("Failed to generate incident id", ex);
        }

        logger.error(
                String.format("Incident ID %s created for %s",
                        incidentId,
                        throwable != null ? throwable.getClass().getSimpleName() : "Unknown Exception"
                ),
                throwable
        );

        return Toast.createWarningToast(String.format(incidentMessage, incidentId));
    }

    private synchronized String generateIncidentId(String deviceId) {
        int lastId = getLastIncidentCount();
        this.incidentCount = ++lastId;
        persistIncidentCount();

        this.incidentId = String.format("%s-%d", deviceId, this.incidentCount);
        return this.incidentId;
    }

    private void persistIncidentCount() {
        try (
                PrintWriter printWriter = new PrintWriter(incidentIdFile);
        ) {
            printWriter.print(this.incidentCount != null ? this.incidentCount : 0);
        } catch (IOException e) {
            logger.warn(String.format("Failed to create '%s' file. Reason: %s", INCIDENT_ID_FILENAME, e.getMessage()), e);
        }

    }

    private void ensureIncidentIdFileExists() {
        if (null == incidentIdFile) {
            incidentIdFile = new File(incidentIdFileLocation, INCIDENT_ID_FILENAME);
        }

        if (!incidentIdFile.exists()) {
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

    public String getIncidentIdFileLocation() {
        return incidentIdFileLocation;
    }

    public void setIncidentIdFileLocation(String incidentIdFileLocation) {
        this.incidentIdFileLocation = incidentIdFileLocation;
    }

    public String getIncidentMessage() {
        return incidentMessage;
    }

    public void setIncidentMessage(String incidentMessage) {
        this.incidentMessage = incidentMessage;
    }
}
