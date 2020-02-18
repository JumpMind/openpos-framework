package org.jumpmind.pos.core.flow.config;

import lombok.Data;
import org.jumpmind.pos.core.flow.ITransitionStep;

import java.util.HashMap;
import java.util.Map;

@Data
public class TransitionStepConfig {
    private Class<? extends ITransitionStep> transitionStepClass;
}
