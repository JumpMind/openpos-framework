package org.jumpmind.pos.translate;

import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.POSSessionInfo;

public interface ITranslator {

    public void handleAction(ITranslationManagerSubscriber subscriber, TranslationManagerServer tmServer, Action action, Form formResults);
    
    public void setPosSessionInfo(POSSessionInfo posSessionInfo);

}
