package org.jumpmind.pos.context.service;

import org.jumpmind.pos.service.EndpointDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/context")
public class ContextService {
    
    @Autowired
    private EndpointDispatcher endpointDispatcher;
    
    @RequestMapping("/config/{configName}")
    public ConfigResult getConfig(
            @RequestParam(value="nodeId", defaultValue="*") String nodeId,
            @PathVariable(value="configName") String configName) {
        return endpointDispatcher.dispatch("/getConfig", nodeId, configName);
    }
    
    @RequestMapping("/node")
    public NodeResult getNode(
            @RequestParam(value="nodeId", defaultValue="*") String nodeId) {
        return endpointDispatcher.dispatch("/node", nodeId);
    }
    
//    final String THIRTY_MINUTES= "1800000";
    
//    @RequestMapping("/{configName}?asDate=true") 
//    public Date getConfigAsDate(
//            @RequestParam(value="nodeId", defaultValue="*") String nodeId,
//            @PathVariable(value="configName") String configName) {
//        Configuration configuration = getConfig(configName);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:SS");
//        try {
//            return format.parse(configuration.getConfigValue());
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return null;
//        }      
//    }
//    
//    @RequestMapping("/config/{configName}?asInt=true")
//    public int getInt(@PathVariable String configName) {
//        Configuration configuration = getConfig(configName);
//        return Integer.valueOf(configuration.getConfigValue());
//    }
//    
//    @RequestMapping("/config/{configName}?asLong=true")
//    public long getLong(@PathVariable String configName) {
//        Configuration configuration = getConfig(configName);
//        return Long.valueOf(configuration.getConfigValue());
//    }
//    
//    @RequestMapping("/config/{configName}")
//    public Configuration getConfig(@PathVariable String configName) {
//            
//        switch (configName) {
//            
//            case "openpos.user.max.login.attempts":  return new Configuration(configName, "4");
//            case "openpos.user.attempts.reset.period.ms":  return new Configuration(configName, THIRTY_MINUTES);
//            default: 
//                Configuration config = new Configuration("", "");
//                config.setResultMessage("Cannot find configuration for name $configName");
//                config.setResultStatus("FAILURE");
//                return config;
//        }
//    }
}
