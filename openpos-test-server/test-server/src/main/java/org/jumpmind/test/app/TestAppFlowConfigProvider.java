package org.jumpmind.test.app;

import org.jumpmind.pos.core.flow.config.YamlConfigProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Primary
@Component
public class TestAppFlowConfigProvider extends YamlConfigProvider {

    public TestAppFlowConfigProvider() {
        super(Arrays.asList("org.jumpmind.test.states"));
    }

    @PostConstruct
    public void init() {

        super.load("loadingscreen", "flows", "LoadingScreenFlow");
        super.load("backtobackdialog", "flows", "BackToBackDialogFlow");
    }
}
