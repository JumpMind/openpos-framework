package org.jumpmind.pos.core.flow;

import static org.jumpmind.pos.util.BoxLogging.HORIZONTAL_LINE;
import static org.jumpmind.pos.util.BoxLogging.HORIZONTAL_MIDDLE;
import static org.jumpmind.pos.util.BoxLogging.LOWER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.LOWER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.VERITCAL_LINE;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.config.SubFlowConfig;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.util.BoxLogging;
import org.slf4j.Logger;

public class StateManagerLogger {

    final Logger log;

    private final String ENTER_SUBSTATE = "<Enter ";
    private final String EXIT_SUBSTATE = "<Exit ";
    
    private final String SUBSTATE_MARGIN = "    ";
    private final String CURRENT_SUBSTATE = " *";

    public StateManagerLogger(Logger log) {
        this.log = log;
    }

    protected void  logStateTransition(Object oldState, Object newState, Action action, String returnAction, SubFlowConfig enterSubState, StateContext exitSubState, ApplicationState applicationState, StateContext resumeSuspendedState) {
        if (oldState == newState) {
            return;
        }
        if (log.isInfoEnabled()) {            
            String oldStateName = oldState != null ? oldState.getClass().getSimpleName() : "<no state>";
            String newStateName = newState.getClass().getSimpleName();
            int box1Width = Math.max(oldStateName.length()+2, 20);
            int box2Width = Math.max(newStateName.length()+2, 20);

            String primaryAction = null;
            String secondaryAction = null;
            if (returnAction == null) {
                primaryAction = action != null ? action.getName() : "";
                secondaryAction = "";
            } else {
                if (!StringUtils.equals(primaryAction, secondaryAction)) {
                    primaryAction = returnAction;
                    secondaryAction = "(caused by " + (action != null ? action.getName() : "") + ")";                    
                } else {
                    primaryAction = action != null ? action.getName() : "";
                    secondaryAction = "";                    
                }
            }

            String enterSubStateTitle = null;
            String exitSubStateTitle = null;

            int inbetweenWidth = Math.max(primaryAction.length()+2, 10);
            inbetweenWidth = Math.max(secondaryAction.length()+2, inbetweenWidth);
            if (enterSubState != null) {
                enterSubStateTitle = ENTER_SUBSTATE + enterSubState.getSubFlowConfig().getName() + ">";
                inbetweenWidth = Math.max(enterSubStateTitle.length()+2, inbetweenWidth);
            } else if (exitSubState != null) {
                exitSubStateTitle = EXIT_SUBSTATE + exitSubState.getFlowConfig().getName() + ">";
                inbetweenWidth = Math.max(exitSubStateTitle.length()+2, inbetweenWidth);
            }

            StringBuilder buff = new StringBuilder(256);

            List<String> subStateStack = buildSubStateStack(applicationState, enterSubState, exitSubState, resumeSuspendedState);

            int LINE_COUNT = 5;
            for (int i = 0; i < LINE_COUNT; i++) {
                switch (i) {
                    case 0:
                        buff.append(drawTop_Line1(box1Width, box2Width, inbetweenWidth, subStateStack));
                        break;
                    case 1:
                        buff.append(drawFillerLine_Line2(box1Width, box2Width, inbetweenWidth, enterSubStateTitle, exitSubStateTitle, subStateStack));
                        break;
                    case 2:
                        buff.append(drawTitle_Line3(box1Width, box2Width,inbetweenWidth, oldStateName, newStateName, subStateStack));
                        break;                    
                    case 3:
                        buff.append(drawEventLine_Line4(box1Width, box2Width,inbetweenWidth, primaryAction, subStateStack));
                        break;
                    case 4:
                        buff.append(drawBottom_Line5(box1Width, box2Width, inbetweenWidth, secondaryAction, subStateStack));
                        break;

                }
            }

            log.info("Transition from " + oldState + " to " + newState + "\n" + buff.toString().trim());
        } else {
            log.info("Transition from " + oldState + " to " + newState);
        }
    }

    public void logTranistionStep(Transition transition, ITransitionStep currentTransitionStep) {

        String stepName = currentTransitionStep.getClass().getSimpleName();
        String stepTitle = "Step: " + stepName;

        String boxed = BoxLogging.box(stepTitle);

        String fromStateName = transition.getSourceStateContext().getState() != null ? transition.getSourceStateContext().getState().getClass().getSimpleName() : "<no state>"; 
        String toStateName = transition.getTargetState() != null ? transition.getTargetState().getClass().getSimpleName() : "<no state>";

        String[] boxLines = boxed.split("\n");

        final String ARROW = " -> ";

        String padding = StringUtils.repeat(' ', fromStateName.length() + ARROW.length());

        boxLines[1] = padding + boxLines[1];
        boxLines[2] = fromStateName + ARROW + boxLines[2] + ARROW + toStateName;
        boxLines[3] = padding + boxLines[3];

        boxed = StringUtils.join(boxLines, "\n");

        log.info("Transition step [" + stepName + "] running between " +
        fromStateName + " and " + toStateName + boxed);
    }


    protected String drawTop_Line1(int box1Width, int box2Width, int inbetweenWidth, List<String> subStateStack) {
        StringBuilder buff = new StringBuilder();

        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box1Width-2)).append(UPPER_RIGHT_CORNER);
        buff.append(StringUtils.repeat(' ', inbetweenWidth));
        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box2Width-2)).append(UPPER_RIGHT_CORNER);
        if (!subStateStack.isEmpty()) {
            buff.append(SUBSTATE_MARGIN).append(subStateStack.get(0));
        }
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawFillerLine_Line2(int box1Width, int box2Width, int inbetweenWidth, String enterSubStateTitle, String exitSubStateTitle, List<String> subStateStack) {
        StringBuilder buff = new StringBuilder();

        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box1Width-2)).append(VERITCAL_LINE);

        if (enterSubStateTitle != null) {
            buff.append(StringUtils.center(enterSubStateTitle, inbetweenWidth));
        } else if (exitSubStateTitle != null) {
            buff.append(StringUtils.center(exitSubStateTitle, inbetweenWidth));
        } else {            
            buff.append(StringUtils.center("", inbetweenWidth));
        }

        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box2Width-2)).append(VERITCAL_LINE);

        if (subStateStack.size() > 1) {
            buff.append(SUBSTATE_MARGIN).append(subStateStack.get(1));
        }

        buff.append("\r\n");

        return buff.toString();
    }

    protected String drawTitle_Line3(int box1Width, int box2Width, int inbetweenWidth, String oldStateName, String newStateName, List<String> subStateStack) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE).append(StringUtils.center(oldStateName, box1Width-2)).append(VERITCAL_LINE);
        buff.append(" ").append(StringUtils.repeat(HORIZONTAL_MIDDLE, inbetweenWidth-3)).append("> ");
        buff.append(VERITCAL_LINE).append(StringUtils.center(newStateName, box2Width-2)).append(VERITCAL_LINE);

        if (subStateStack.size() > 2) {
            buff.append(SUBSTATE_MARGIN).append(subStateStack.get(2));
        }

        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawEventLine_Line4(int box1Width, int box2Width, int inbetweenWidth, String actionName, List<String> subStateStack) {
        StringBuilder buff = new StringBuilder();

        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box1Width-2)).append(VERITCAL_LINE);
        buff.append(StringUtils.center(actionName, inbetweenWidth));
        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box2Width-2)).append(VERITCAL_LINE);

        if (subStateStack.size() > 3) {
            buff.append(SUBSTATE_MARGIN).append(subStateStack.get(3));
        }

        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawBottom_Line5(int box1Width, int box2Width, int inbetweenWidth, String secondaryAction, List<String> subStateStack) {
        StringBuilder buff = new StringBuilder();

        buff.append(LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box1Width-2)).append(LOWER_RIGHT_CORNER);
        buff.append(StringUtils.center(secondaryAction, inbetweenWidth));
        buff.append(LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box2Width-2)).append(LOWER_RIGHT_CORNER);

        if (subStateStack.size() > 4) {
            buff.append(SUBSTATE_MARGIN).append(subStateStack.get(4));
        }

        buff.append("\r\n");
        return buff.toString();
    }

    protected List<String> buildSubStateStack(ApplicationState applicationState, SubFlowConfig enterSubState, StateContext exitSubState, StateContext resumeSuspendedState) {

        List<String> subStackStack = 
                applicationState.getStateStack().stream().map(context -> " " + context.getFlowConfig().getName()).
                collect(Collectors.toList());

        subStackStack.add(0, " " + applicationState.getCurrentContext().getFlowConfig().getName());

        if (enterSubState != null) {
            subStackStack.add(0, " " + enterSubState.getSubFlowConfig().getName());    
        } else if (exitSubState != null) {
            subStackStack.remove(0);
            if (resumeSuspendedState != null) {
                subStackStack.add(0, " " + resumeSuspendedState.getFlowConfig().getName());
            }
        }
        
        if (!subStackStack.isEmpty()) {            
            subStackStack.set(0, CURRENT_SUBSTATE + subStackStack.get(0).trim());
            subStackStack.add(0, "Substates:");
        }

        return subStackStack;
    }    


}
