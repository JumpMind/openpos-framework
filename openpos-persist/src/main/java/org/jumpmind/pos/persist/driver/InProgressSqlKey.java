package org.jumpmind.pos.persist.driver;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class InProgressSqlKey {

    private PreparedStatementWrapper psWrapper;
    private String threadName;
    private long salt;

}
