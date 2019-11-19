package org.jumpmind.pos.persist.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.*;

import org.apache.commons.io.IOUtils;
import org.jumpmind.db.model.TypeMap;
import org.jumpmind.db.platform.postgresql.PostgresLobHandler;
import org.jumpmind.db.sql.JdbcSqlTemplate;
import org.jumpmind.db.sql.Row;
import org.jumpmind.db.sql.SqlException;
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
            String key = lookupColumnName(rsMetaData, i);
            Object obj = getResultSetValue(rs, rsMetaData, i, false, true);
            mapOfColValues.put(key, obj);
        }
        return mapOfColValues;
    }

    private final String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex)
            throws SQLException {
        String name = resultSetMetaData.getColumnLabel(columnIndex);
        if (name == null || name.length() < 1) {
            name = resultSetMetaData.getColumnName(columnIndex);
        }
        return name;
    }

    private final Object getResultSetValue(ResultSet rs, ResultSetMetaData metaData, int index, boolean readStringsAsBytes, boolean returnLobObjects) throws SQLException {
        if (metaData == null) {
            metaData = rs.getMetaData();
        }
        Object obj = null;
        int jdbcType = metaData.getColumnType(index);
        String jdbcTypeName = metaData.getColumnTypeName(index);
        if (readStringsAsBytes && TypeMap.isTextType(jdbcType)) {
            byte[] bytes = rs.getBytes(index);
            if (bytes != null) {
                obj = new String(bytes);
            }
        } else {
            obj = rs.getObject(index);
        }
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob && !returnLobObjects) {
            Blob blob = (Blob) obj;
            try(InputStream is = blob.getBinaryStream()) {
                obj = IOUtils.toByteArray(is);
            } catch (IOException e) {
                throw new SqlException(e);
            }
        } else if (obj instanceof Clob && !returnLobObjects) {
            Clob clob = (Clob) obj;
            try(Reader reader = clob.getCharacterStream()) {
                obj = IOUtils.toString(reader);
            } catch (IOException e) {
                throw new SqlException(e);
            }
        } else if (className != null && ("oracle.sql.TIMESTAMP".equals(className))) {
            obj = rs.getTimestamp(index);
        } else if (className != null && "oracle.sql.TIMESTAMPTZ".equals(className)) {
            obj = rs.getString(index);
        } else if (className != null && "oracle.sql.TIMESTAMPLTZ".equals(className)) {
            obj = rs.getString(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = metaData.getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName)
                    || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj instanceof java.sql.Date) {
            String metaDataClassName = metaData.getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            }
        } else if (obj instanceof Timestamp) {
            String typeName = metaData.getColumnTypeName(index);
            if (typeName != null && typeName.equals("timestamptz")) {
                obj = rs.getString(index);
            }
        }  else if (jdbcTypeName != null && "oid".equals(jdbcTypeName)) {
            obj = PostgresLobHandler.getLoColumnAsBytes(rs, index);
        }
        return obj;
    }

}