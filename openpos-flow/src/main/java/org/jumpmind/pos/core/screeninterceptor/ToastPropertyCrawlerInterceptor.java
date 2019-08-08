package org.jumpmind.pos.core.screeninterceptor;


import java.util.List;

import org.jumpmind.pos.core.ui.Toast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("device")
public class ToastPropertyCrawlerInterceptor extends AbstractMessagePropertyCrawlerInterceptor<Toast> {
    @Autowired(required = false)
    List<IMessagePropertyStrategy<Toast>> toastPropertyStrategies;

    @Override
    public List<IMessagePropertyStrategy<Toast>> getMessagePropertyStrategies() {
        return toastPropertyStrategies;
    }

    @Override
    public void setMessagePropertyStrategies(List<IMessagePropertyStrategy<Toast>> strategies) {
        this.toastPropertyStrategies = strategies;
    }

}
