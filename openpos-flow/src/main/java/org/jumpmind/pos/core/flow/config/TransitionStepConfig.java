package org.jumpmind.pos.core.flow.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.flow.ITransitionStep;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransitionStepConfig {
    private Class<? extends ITransitionStep> transitionStepClass;
}
