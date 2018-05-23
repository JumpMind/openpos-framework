package org.jumpmind.pos.core.flow;

import static org.jumpmind.pos.util.BoxLogging.DOWN_ARROW;
import static org.jumpmind.pos.util.BoxLogging.HORIZONTAL_LINE;
import static org.jumpmind.pos.util.BoxLogging.LOWER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.LOWER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.VERITCAL_LINE;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class StateManagerLogger {
    
    final Logger log;
    
    public StateManagerLogger(Logger log) {
        this.log = log;
    }
    
    protected void logStateTransition(IState oldState, IState newState, Action action, boolean enterSubState) {
        if (oldState == newState) {
            return;
        }
        if (log.isInfoEnabled()) {            
            String oldStateName = oldState != null ? oldState.getClass().getSimpleName() : "<no state>";
            String newStateName = newState.getClass().getSimpleName();
            int box1Width = Math.max(oldStateName.length()+2, 20);
            int box2Width = Math.max(newStateName.length()+2, 20);
            
            int inbetweenWidth = action != null ? Math.max(action.getName().length()+2, 10) : 10;
            
//            boolean exitSubState = isTerminatingAction(action);
            boolean exitSubState = false;
            
            StringBuilder buff = new StringBuilder(256);
            
            int LINE_COUNT = 5;
            for (int i = 0; i < LINE_COUNT; i++) {
                switch (i) {
                    case 0:
                        buff.append(drawTop(box1Width, box2Width, inbetweenWidth));
                        break;
                    case 1:
                        buff.append(drawFillerLine(box1Width, box2Width, inbetweenWidth, enterSubState, exitSubState));
                        break;
                    case 2:
                        buff.append(drawTitleLine(box1Width, box2Width,inbetweenWidth, oldStateName, newStateName));
                        break;                    
                    case 3:
                        buff.append(drawEventLine(box1Width, box2Width,inbetweenWidth, action != null ? action.getName() : ""));
                        break;
                    case 4:
                        buff.append(drawBottom(box1Width, box2Width, inbetweenWidth));
                        break;                    
                        
                }
            }
            
            log.info("Transition from " + oldState + " to " + newState + "\n" + buff.toString());
        } else {
            log.info("Transition from " + oldState + " to " + newState);
        }
    }

    protected String drawTop(int box1Width, int box2Width, int inbetweenWidth) {
        StringBuilder buff = new StringBuilder();
        
        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box1Width-2)).append(UPPER_RIGHT_CORNER);
        buff.append(StringUtils.repeat(' ', inbetweenWidth));
        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box2Width-2)).append(UPPER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }
    
    protected String drawFillerLine(int box1Width, int box2Width, int inbetweenWidth, boolean enterSubState, boolean exitSubState) {
        StringBuilder buff = new StringBuilder();
        
        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box1Width-2)).append(VERITCAL_LINE);
        
        if (exitSubState) {
            buff.append(StringUtils.center("<Exit SubState>", inbetweenWidth));
        } else {            
            buff.append(StringUtils.center("", inbetweenWidth));
        }
        
        if (enterSubState) {            
            buff.append(VERITCAL_LINE).append(StringUtils.center(DOWN_ARROW + " SubState", box2Width-2)).append(VERITCAL_LINE);
        } else {
            buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box2Width-2)).append(VERITCAL_LINE);            
        }
        buff.append("\r\n");
        return buff.toString();
    }
    
    protected String drawEventLine(int box1Width, int box2Width, int inbetweenWidth, String actionName) {
        StringBuilder buff = new StringBuilder();
        
        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box1Width-2)).append(VERITCAL_LINE);
        buff.append(StringUtils.center(actionName, inbetweenWidth));
        buff.append(VERITCAL_LINE).append(StringUtils.repeat(' ', box2Width-2)).append(VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }
    
    
    protected String drawTitleLine(int box1Width, int box2Width, int inbetweenWidth, String oldStateName, String newStateName) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE).append(StringUtils.center(oldStateName, box1Width-2)).append(VERITCAL_LINE);
        buff.append(" ").append(StringUtils.repeat(HORIZONTAL_LINE, inbetweenWidth-3)).append("> ");
        buff.append(VERITCAL_LINE).append(StringUtils.center(newStateName, box2Width-2)).append(VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }
    
    protected String drawBottom(int box1Width, int box2Width, int inbetweenWidth) {
        StringBuilder buff = new StringBuilder();
        
        buff.append(LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box1Width-2)).append(LOWER_RIGHT_CORNER);
        buff.append(StringUtils.repeat(' ', inbetweenWidth));
        buff.append(LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, box2Width-2)).append(LOWER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }

}
