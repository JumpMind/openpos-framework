package org.jumpmind.pos.persist.impl;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.sql.InvalidSqlException;

import java.util.List;

public abstract class AbstractSqlTemplates {

    public abstract void replaceModelClassNamesWithTableNames(DatabaseSchema dbSchema, List<Class<?>> modelClassList, boolean validateTablesInQueries);
    protected abstract String getTemplateSuffix();

    protected String replaceClassModelNameInSqlText(String sqlText, String modelClassName, String tableName)  {
        if ((sqlText != null) && StringUtils.indexOf(sqlText, modelClassName) >= 0)  {
            return sqlText.replaceAll("\\b" + modelClassName + "\\b", tableName);
        }

        return sqlText;
    }

    protected void scanSqlTextForLiteralTableName(String sqlText, String textType, String templateName, String regularTableName, String shadowTableName, String modulePrefix, String modelClassName)  {
        if (sqlText != null) {
            boolean textContainsRegularName = sqlText.matches(".*\\b" + regularTableName + "\\b.*");
            boolean textContainsShadowName = false;
            if (!regularTableName.equals(shadowTableName)) {
                textContainsShadowName = sqlText.matches(".*\\b" + shadowTableName + "\\b.*");
            }

            if (textContainsRegularName || textContainsShadowName) {
                throw new InvalidSqlException("SQL " + (StringUtils.isNotBlank(textType) ? textType + " " : "") + "contains literal table name " +
                        (textContainsRegularName ? regularTableName : shadowTableName) + " in query " + templateName +
                        " in file " + modulePrefix.toLowerCase() + getTemplateSuffix() + ". Replace it with the corresponding model class name " +
                        modelClassName + " instead."
                );
            }
        }
    }

}
