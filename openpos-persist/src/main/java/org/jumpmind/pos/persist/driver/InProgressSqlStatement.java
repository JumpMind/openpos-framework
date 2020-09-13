package org.jumpmind.pos.persist.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class InProgressSqlStatement {

    String sql;
    java.util.List<Object> args;
    long startTime;
    long lastLoggedTime;
    String threadName;

    public InProgressSqlStatement(String sql, List<Object> args, long startTime, String threadName) {
        this.sql = sql;
        this.args = args;
        this.startTime = startTime;
        this.threadName = threadName;
        lastLoggedTime = System.currentTimeMillis();
    }

}
