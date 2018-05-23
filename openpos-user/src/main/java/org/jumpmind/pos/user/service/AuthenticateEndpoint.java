package org.jumpmind.pos.user.service;

import java.util.Date;

import org.apache.commons.collections4.CollectionUtils;
import org.jumpmind.pos.context.service.ContextService;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.user.model.PasswordHistory;
import org.jumpmind.pos.user.model.User;
import org.jumpmind.pos.user.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional(transactionManager="userTxManager")
public class AuthenticateEndpoint {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ContextService contextService;

    @Endpoint("/authenticate")
//    @Cached("userCache")
    public AuthenticationResult authenticate(
            @RequestParam(value="nodeId", defaultValue="") String nodeId,
            @RequestParam(value="locale", defaultValue="") String locale,
            @RequestParam(value="username", defaultValue="") String username,
            @RequestParam(value="password", defaultValue="") String password) {

        User user = userRepository.findUser(username);

        if (user != null) {
            if (!checkPassword(user, password)) {
                return failAuthentication(user, "Incorrect Password", "PASSWORD_INCORRECT");
            } else if (checkLockout(user)) {
                return failAuthentication(user, "User locked out.", "USER_LOCKOUT");
            } else if (user.isPasswordExpiredFlag()) {
                return handlePasswordExpired(user);
            } else {
                return handleSuccess(user);
            }
        }
        else  {
            return failAuthentication(user, "User not found.", "GENERAL_FAILURE");              
        }
    }

    private AuthenticationResult handleSuccess(User user) {
        AuthenticationResult result =  new AuthenticationResult("SUCCESS", user);
        long expiresInDays = passwordExpiresInDays(user);
        // TOO
//        if (expiresInDays > 0 && expiresInDays <= contextService.getInt("openpos.user.warn.password.expires.days")) {
        if (expiresInDays > 0 && expiresInDays <= 7) {
            UserMessage message = new UserMessage();
            message.setMessageCode("PASSWORD_EXPIRY_WARNING");
            message.setMessage(String.format("Your password exires in %s days.", expiresInDays));
            result.getUserMessages().add(message);
            
        }
        return result;
    }

    private AuthenticationResult handlePasswordExpired(User user) {
        UserMessage message = new UserMessage();
        long expiresInDays = Math.abs(passwordExpiresInDays(user));
        message.setMessageCode("PASSWORD_EXPIRED");
        if (expiresInDays > 0) {                    
            message.setMessage(String.format("Your password expired %s day(s) ago.", expiresInDays));                
        } else {
            message.setMessage(String.format("Your password has expired.", expiresInDays));
        }
        AuthenticationResult result = failAuthentication(user, "Password expired.", "PASSWORD_EXPIRED");
        result.getUserMessages().add(message);
        return result;
    }

    protected boolean checkPassword(User user, String password) {
        if (password.length() < 3) {
            user.setPasswordFailedAttempts(user.getPasswordFailedAttempts()+1);
            user.setLastPasswordAttempt(new Date());
// TODO
//            if (user.getPasswordFailedAttempts() > contextService.getInt("openpos.user.max.login.attempts")) {
            if (user.getPasswordFailedAttempts() > 5) {
                user.setLockedOutFlag(true);
            }
            userRepository.save(user);
            return false;
        }
        return true;
    }

    protected boolean checkLockout(User user) {
        if (user.isLockedOutFlag()) {
            Date lastPasswordAttempt = user.getLastPasswordAttempt();
            if (lastPasswordAttempt != null) {      
                // TODO
//                Date passwordAttemptsResetDate = new Date(lastPasswordAttempt.getTime() + 
//                        contextService.getLong("openpos.user.attempts.reset.period.ms"));
                Date passwordAttemptsResetDate = new Date(lastPasswordAttempt.getTime() + 
                      5000);                
                Date now = new Date();
                if (now.after(passwordAttemptsResetDate)) {                    
                    user.setLockedOutFlag(false);
                    user.setPasswordFailedAttempts(0);
                    userRepository.save(user);
                }
            }
        }
        return user.isLockedOutFlag();
    }

    protected AuthenticationResult failAuthentication(User user, String message, String code) {
        AuthenticationResult result = new AuthenticationResult("FAILURE");
        result.setAuthenticationCode(code);
        result.setResultMessage(message);
        return result;       
    }
    
    protected long passwordExpiresInDays(User user) {
        if (CollectionUtils.isEmpty(user.getPasswordHistory())) {
            return -1;
        }
        
        PasswordHistory passwordHistory = user.getPasswordHistory().get(0);
        if (passwordHistory.getExpirationTime() != null) {
            Date now = new Date(); // TODO the server may not be the same timezone as client.
            long diffDays = org.jumpmind.pos.service.util.DateUtils.daysBetween(now, passwordHistory.getExpirationTime());
            return diffDays;
        }
        
        return -1;
    }

}
