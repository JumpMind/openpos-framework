package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.util.LogFormatter;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.jumpmind.pos.util.BoxLogging.*;

@Component
public class LoggingStateManagerObserver implements IStateManagerObserver {

    final Logger actionLogger = LoggerFactory.getLogger(StateManager.class.getName() + ".action.graphical");
    final Logger screenLogger = LoggerFactory.getLogger(StateManager.class.getName() + ".screen.graphical");
    final StateManagerLogger stateManagerLogger = new StateManagerLogger(LoggerFactory.getLogger(StateManager.class.getName() + ".graphical"));

    static final String LIGHTNING_AND_THE_THUNDER =
            "__/\\\n" +
            "\\  _\\   %s\n" +
            " \\/ ";

    @Autowired
    LogFormatter logFormatter;

    @Override
    public void onTransition(ApplicationState applicationState, Transition transition, Action action, String returnActionName) {
        stateManagerLogger.logStateTransition(applicationState.getCurrentContext().getState(), transition.getTargetState(), action, returnActionName,
                transition.getEnterSubStateConfig(), transition.isExitingSubstate() ? applicationState.getCurrentContext() : null, applicationState,
                transition.getResumeSuspendedState());
    }

    @Override
    public void onAction(ApplicationState applicationState, Action action) {
        if (action.isDeviceOriginationFlag() && actionLogger.isInfoEnabled()) {
            boolean debug = actionLogger.isDebugEnabled();
            actionLogger.info("Received action from {}{}\n{}", applicationState.getDeviceId(),
                    debug ? "" : " "+logFormatter.toCompactJsonString(action),
                    formatAction(action, debug));
        }
    }

    @Override
    public void onScreen(ApplicationState applicationState, UIMessage screen) {
        if (screenLogger.isInfoEnabled()) {
            screenLogger.info("Show screen on device \"" + applicationState.getDeviceId() + "\" (" + screen.getClass().getName() + ")\n"
                    + drawBox(screen.getId(), screen.getScreenType()));
        }
    }

    public void onTransition(ApplicationState applicationState, Transition transition, ITransitionStep currentTransitionStep) {
        stateManagerLogger.logTranistionStep(transition, currentTransitionStep);
    }

    public String formatAction(Action action, boolean debugFlag) {
        return String.format(LIGHTNING_AND_THE_THUNDER + "%s", action.getName(),
                debugFlag ? "\n"+logFormatter.toJsonString(action): "");
    }

    protected String drawBox(String name, String typeName) {
        String displayName = name != null ? name : null;
        String displayTypeName = "";

        if (!StringUtils.isEmpty(displayName)) {
            displayTypeName = typeName != null ? typeName : "screen";
            displayTypeName = "[" + displayTypeName + "]";
        } else {
            displayName = typeName != null ? typeName : "screen";
            displayName = "[" + displayName + "]";
        }

        int boxWidth = Math.max(Math.max(displayName.length() + 2, 50), displayTypeName.length() + 4);
        final int LINE_COUNT = 8;
        StringBuilder buff = new StringBuilder(256);
        for (int i = 0; i < LINE_COUNT; i++) {
            switch (i) {
                case 0:
                    buff.append(drawTop1(boxWidth + 2));
                    break;
                case 1:
                    buff.append(drawTop2(boxWidth));
                    break;
                case 3:
                    buff.append(drawTitleLine(boxWidth, displayName));
                    break;
                case 4:
                    buff.append(drawTypeLine(boxWidth, displayTypeName));
                    break;
                case 5:
                    buff.append(drawBottom1(boxWidth));
                    break;
                case 6:
                    buff.append(drawBottom2(boxWidth + 2));
                    break;
            }
        }
        return buff.toString().trim();
    }

    protected String drawTop1(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 2)).append(UPPER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTop2(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 4))
                .append(UPPER_RIGHT_CORNER + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawFillerLine(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + VERITCAL_LINE).append(StringUtils.repeat(' ', boxWidth - 4))
                .append(VERITCAL_LINE + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTitleLine(int boxWidth, String name) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + VERITCAL_LINE).append(StringUtils.center(name, boxWidth - 4))
                .append(VERITCAL_LINE + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTypeLine(int boxWidth, String typeName) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + VERITCAL_LINE).append(StringUtils.center(typeName, boxWidth - 4))
                .append(VERITCAL_LINE + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawBottom1(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 4))
                .append(LOWER_RIGHT_CORNER + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawBottom2(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 2)).append(LOWER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }

}
