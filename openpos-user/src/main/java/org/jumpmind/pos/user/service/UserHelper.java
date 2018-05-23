package org.jumpmind.pos.user.service;

import org.apache.commons.collections4.CollectionUtils;
import org.jumpmind.pos.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

    public String getCurrentPassword(User user) {
        if (!CollectionUtils.isEmpty(user.getPasswordHistory())) {            
            return user.getPasswordHistory().get(0).getHashedPassword();
        } else {
            return null;
        }
    }
    
}
