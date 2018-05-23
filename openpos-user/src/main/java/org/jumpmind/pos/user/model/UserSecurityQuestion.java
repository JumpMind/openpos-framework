package org.jumpmind.pos.user.model;

import java.util.Date;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(
        name = "operator_security_question",
        description = "A record of the Operator's answer to a security question that is put to the Operator when they have forgotten their Password and the system is trying to verify their identity and allow a new Password to be set.")
public class UserSecurityQuestion extends Entity {

    @Column(primaryKey = true,
            size = "16",
            description = "The corresponding SecurityQuestion id.")
    private String securityQuestionRowId;

    @Column(required = true,
            size = "255",
            description = "A record of the Operator's answer to the indicated SecurityQuestion.")
    private String answerText;

    @Column(description = "The date and time at which this OperatorSecurityQuestion was chosen by the Operator.")
    Date effectiveTime;

    @Column(description = "The date and time at which this OperatorSecurityQuestion ceases to be valie because a new SecurityHint has been chosen.")
    Date expirationTime;

    public String getSecurityQuestionRowId() {
        return securityQuestionRowId;
    }

    public void setSecurityQuestionRowId(String securityQuestionRowId) {
        this.securityQuestionRowId = securityQuestionRowId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

}
