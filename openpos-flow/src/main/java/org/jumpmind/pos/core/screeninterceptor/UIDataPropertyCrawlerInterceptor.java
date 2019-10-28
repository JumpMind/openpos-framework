package org.jumpmind.pos.core.screeninterceptor;

import org.jumpmind.pos.core.ui.UIDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("device")
public class UIDataPropertyCrawlerInterceptor<T> extends AbstractMessagePropertyCrawlerInterceptor<UIDataMessage<T>> {

    @Autowired(required = false)
    List<IMessagePropertyStrategy<UIDataMessage<T>>> uiDataPropertyStrategies;


    @Override
    public List<IMessagePropertyStrategy<UIDataMessage<T>>> getMessagePropertyStrategies() {
        return uiDataPropertyStrategies;
    }

    @Override
    public void setMessagePropertyStrategies(List<IMessagePropertyStrategy<UIDataMessage<T>>> iMessagePropertyStrategies) {
        this.uiDataPropertyStrategies = iMessagePropertyStrategies;
    }
}
