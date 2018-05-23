package org.jumpmind.pos.user.model;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(
        name = "security_question",
        description = "A question that is put ot the Customer or Operator when they have forgotten their Password and the system is trying to verify their identity.")
public class SecurityQuestion extends Entity {

    @Column(primaryKey = true)
    private String securityQuestionId;

    @Column(required = true,
            size = "255",
            description = "A question that is put to a Customer or Operator when they have forgotten their Password and the system is trying to verify their identity and allow a new Password to be set.")
    private String questionText;

}
