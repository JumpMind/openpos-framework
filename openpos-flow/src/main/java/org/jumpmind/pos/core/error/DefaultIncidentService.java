package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.flow.ErrorGlobalActionHandler;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.message.ErrorDialogUIMessage;
import org.jumpmind.pos.util.model.Message;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.function.BiFunction;

@Slf4j
@ConfigurationProperties(prefix = "openpos.incident")
@Component
public class DefaultIncidentService implements IIncidentService {

    private static final String INCIDENT_ID_FILENAME = ".incident_id";

    private String incidentIdFileLocation;
    private BiFunction<IncidentContext, String, String> incidentMessageFn = 
        (ctx, incId) -> String.format("Unfortunately, an unexpected error has occurred. Please report this incident using the following id: %s.", incId);

    private String incidentTitle = "An Unexpected Error Occurred";
    private String incidentButtonTitle = "Continue";
    private String incidentImageUrl = "content:error";
    private String incidentAction = ErrorGlobalActionHandler.RESET_STATE_MANAGER;
    private String incidentMessage = null;
    
    private File incidentIdFile;
    private Integer incidentCount;
    private String incidentId;

    @Override
    public Message createIncident(Throwable throwable, IncidentContext incidentContext) {
        String incId = generateIncidentId(incidentContext.getDeviceId());
        log.error(
                String.format("Incident ID %s created for %s",
                        incId,
                        throwable != null ? throwable.getClass().getSimpleName() : "Unknown Exception"
                ),
                throwable
        );

        return ErrorDialogUIMessage.builder().
                title(getIncidentTitle()).
                message(resolveIncidentMessage(incidentContext, incId)).
                imageUrl(getIncidentImageUrl()).
                button(ActionItem.builder().
                        enabled(true).
                        title(getIncidentButtonTitle()).
                        action(getIncidentAction()).build()).build();
    }

    public String getIncidentIdFileLocation() {
        return incidentIdFileLocation;
    }

    public void setIncidentIdFileLocation(String incidentIdFileLocation) {
        this.incidentIdFileLocation = incidentIdFileLocation;
    }

    public String getIncidentMessage() {
        return this.incidentMessage;
    }
    
    public void setIncidentMessage(String message) {
        this.incidentMessage = message;
    }
    
    public BiFunction<IncidentContext, String, String> getIncidentMessageFn() {
        return this.incidentMessageFn;
    }

    /**
     * Expects to receive a function that accepts two parameters.  Parameter 1 is
     * the {@link IncidentContext}. Parameter 2 is a String reference to the 
     * incident id. The string returned by the given function will be used for
     * display in the UI.
     * @param messageFn The function as described used to generate an error message
     * for display to the user.
     */
    public void setIncidentMessageFn(BiFunction<IncidentContext, String, String> messageFn) {
        this.incidentMessageFn = messageFn;
    }

    /**
     * First returns the incidentMessage if it is set.  If incidentMessage is not
     * set, and incidentMessageFn is set, returns the results of applying the
     * incidentMessageFn to the given arguments.  Otherwise returns {@code null}.
     * @param ctx The IncidentContext
     * @param incidentId The identifier of the incident being generated.
     * @return A string message to display on the UI describing the incident.
     */
    protected String resolveIncidentMessage(IncidentContext ctx, String incidentId) {
        String msg = this.getIncidentMessage();
        if (msg != null) {
            return msg;
        }
        
        BiFunction<IncidentContext, String, String> fn = this.getIncidentMessageFn();
        if (fn != null) {
            return fn.apply(ctx, incidentId);
        }
        
        return null;
    }
    
    public String getIncidentTitle() {
        return incidentTitle;
    }

    public void setIncidentTitle(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }

    public String getIncidentButtonTitle() {
        return incidentButtonTitle;
    }

    public void setIncidentButtonTitle(String incidentButtonTitle) {
        this.incidentButtonTitle = incidentButtonTitle;
    }

    public String getIncidentImageUrl() {
        return incidentImageUrl;
    }

    public void setIncidentImageUrl(String incidentImageUrl) {
        this.incidentImageUrl = incidentImageUrl;
    }
    
    public String getIncidentAction() {
        return incidentAction;
    }

    public void setIncidentAction(String incidentAction) {
        this.incidentAction = incidentAction;
    }

    protected synchronized String generateIncidentId(String deviceId) {
        try {
            int lastId = getLastIncidentCount();
            this.incidentCount = ++lastId;
            persistIncidentCount();
    
            this.incidentId = String.format("%s-%d", deviceId, this.incidentCount);
            return this.incidentId;
        } catch (Exception ex) {
            log.warn("Failed to generate incident id", ex);
            return "unknown";
        }
    }

    private void persistIncidentCount() {
        try (
                PrintWriter printWriter = new PrintWriter(incidentIdFile);
        ) {
            printWriter.print(this.incidentCount != null ? this.incidentCount : 0);
        } catch (IOException e) {
            log.warn(String.format("Failed to create '%s' file. Reason: %s", INCIDENT_ID_FILENAME, e.getMessage()), e);
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

}
