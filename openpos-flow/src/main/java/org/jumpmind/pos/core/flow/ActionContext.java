package org.jumpmind.pos.core.flow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.server.model.Action;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionContext {

    Action action;
    StackTraceElement[] stackTrace;

    public ActionContext(Action action) {
        this.action = action;
    }

}
