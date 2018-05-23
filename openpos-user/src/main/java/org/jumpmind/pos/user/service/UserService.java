package org.jumpmind.pos.user.service;

import org.jumpmind.pos.service.EndpointDispatcher;
import org.jumpmind.pos.service.ServiceContext;
import org.jumpmind.pos.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserService {
    
    @Autowired
    private EndpointDispatcher endpointDispatcher;
    
    @RequestMapping("/authenticate")
    public AuthenticationResult authenticate(
            @RequestParam(value="nodeId", defaultValue="") String nodeId,
            @RequestParam(value="locale", defaultValue="") String locale,
            @RequestParam(value="username", defaultValue="") String username,
            @RequestParam(value="password", defaultValue="") String password) {
        return endpointDispatcher.dispatch("/authenticate", nodeId, locale, username, password);
    }
    
    @RequestMapping("/changePassword")
    public ServiceResult changePassword(
            @RequestParam(value="nodeId", defaultValue="") String nodeId,
            @RequestParam(value="locale", defaultValue="") String locale,            
            @RequestParam(value="username", defaultValue="") String username,
            @RequestParam(value="oldPassword", defaultValue="") String oldPassword,
            @RequestParam(value="newPassword1", defaultValue="") String newPassword1,
            @RequestParam(value="newPassword2", defaultValue="") String newPassword2) {
        return endpointDispatcher.dispatch("/changePassword", nodeId, locale, username, oldPassword, newPassword1, newPassword2);
    }  
}
