package org.jumpmind.pos.persist.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jumpmind.db.sql.JdbcSqlTemplate;
import org.jumpmind.db.sql.Row;
import org.springframework.jdbc.core.RowMapper;

public class DefaultMapper implements RowMapper<Row> {

    ResultSetMetaData rsMetaData = null;
    int rsColumnCount;

    @Override
    public Row mapRow(ResultSet rs, int rowNum) throws SQLException {

        if (rsMetaData == null) {
            rsMetaData = rs.getMetaData();
            rsColumnCount = rsMetaData.getColumnCount();
        }

        Row mapOfColValues = new Row(rsColumnCount);
        for (int i = 1; i <= rsColumnCount; i++) {
            String key = JdbcSqlTemplate.lookupColumnName(rsMetaData, i);
            Object obj = JdbcSqlTemplate.getResultSetValue(rs, rsMetaData, i, false);
            mapOfColValues.put(key, obj);
        }
        return mapOfColValues;
    }
}