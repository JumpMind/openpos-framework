package org.jumpmind.pos.service.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigServiceTest implements IConfigService {
    
    final String THIRTY_MINUTES= "1800000";
    
    @RequestMapping("/configtest/{configName}?asDate=true") 
    public Date getDate(@PathVariable String configName) {
        Configuration configuration = getConfig(configName);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:SS");
        try {
            return format.parse(configuration.getConfigValue());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }      
    }
    
    @RequestMapping("/configtest/{configName}?asInt=true")
    public int getInt(@PathVariable String configName) {
        Configuration configuration = getConfig(configName);
        return Integer.valueOf(configuration.getConfigValue());
    }
    
    @RequestMapping("/configtest/{configName}?asLong=true")
    public long getLong(@PathVariable String configName) {
        Configuration configuration = getConfig(configName);
        return Long.valueOf(configuration.getConfigValue());
    }
    
    @RequestMapping("/configtest/{configName}")
    public Configuration getConfig(@PathVariable String configName) {
            
        switch (configName) {
            case "openpos.user.max.login.attempts":  return new Configuration(configName, "4");
            case "openpos.user.attempts.reset.period.ms":  return new Configuration(configName, THIRTY_MINUTES);
            case "openpos.user.warn.password.expires.days":  return new Configuration(configName, "7");
            default: 
                Configuration config = new Configuration("", "");
                config.setResultMessage("Cannot find configuration for name $configName");
                config.setResultStatus("FAILURE");
                return config;
        }
    }
}
