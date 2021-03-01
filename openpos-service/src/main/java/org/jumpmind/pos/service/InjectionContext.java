package org.jumpmind.pos.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InjectionContext {

    private Object[] arguments;

    public InjectionContext(Object[] arguments) {
        super();
        this.arguments = arguments;
    }


}
