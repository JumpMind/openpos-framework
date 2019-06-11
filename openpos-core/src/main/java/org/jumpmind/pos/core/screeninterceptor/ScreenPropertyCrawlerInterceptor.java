package org.jumpmind.pos.core.screeninterceptor;


import java.util.List;

import org.jumpmind.pos.core.ui.UIMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("device")
public class ScreenPropertyCrawlerInterceptor extends AbstractMessagePropertyCrawlerInterceptor<UIMessage> {

    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired(required = false)
    List<IMessagePropertyStrategy<UIMessage>> screenPropertyStrategies;

    @Override
    public List<IMessagePropertyStrategy<UIMessage>> getMessagePropertyStrategies() {
        return screenPropertyStrategies;
    }

    @Override
    public void setMessagePropertyStrategies(List<IMessagePropertyStrategy<UIMessage>> strategies) {
        this.screenPropertyStrategies = strategies;
    }

}
