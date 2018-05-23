package org.jumpmind.pos.i18n.service;

import org.jumpmind.pos.service.EndpointDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/i18n")
public class i18nService {
    
    @Autowired
    private EndpointDispatcher endpointDispatcher;
    
    @RequestMapping("/getString")
    public String getString(
            @RequestParam(value="base", defaultValue="") String base,
            @RequestParam(value="key", defaultValue="") String key,
            @RequestParam(value="locale", defaultValue="") String locale,
            @RequestParam(value="brand", defaultValue="") String brand,
            @RequestParam(value="args", defaultValue="") Object... args) {
        return endpointDispatcher.dispatch("/getString", base, key, locale, brand, args);
    }      
}
