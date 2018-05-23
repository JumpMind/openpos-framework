package org.jumpmind.pos.user.service;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.service.ServiceResult;
import org.jumpmind.pos.user.model.User;

public class AuthenticationResult extends ServiceResult {
    
    private String authenticationCode;
    private User user;
    private List<UserMessage> userMessages = new ArrayList<>();

    public AuthenticationResult() {
        
    }
    
    public AuthenticationResult(String resultStatus) {
        setResultStatus(resultStatus);
    }
    public AuthenticationResult(String resultStatus, User user) {
        setResultStatus(resultStatus);
        this.user = user;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }
    
    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserMessage> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(List<UserMessage> userMessages) {
        this.userMessages = userMessages;
    }

}
