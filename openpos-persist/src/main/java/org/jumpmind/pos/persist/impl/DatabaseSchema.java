package org.jumpmind.pos.persist.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.IIndex;
import org.jumpmind.db.model.IndexColumn;
import org.jumpmind.db.model.NonUniqueIndex;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.model.UniqueIndex;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.IDdlBuilder;
import org.jumpmind.db.sql.SqlScript;
import org.jumpmind.pos.persist.*;
import org.jumpmind.pos.util.model.ITypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseSchema {

    protected static final Logger log = LoggerFactory.getLogger(DatabaseSchema.class);

    private IDatabasePlatform platform;
    private List<Class<?>> entityClasses;
    private List<Class<?>> entityExtensionClasses;
    private Map<Class<?>, ModelMetaData> classToModelMetaData = new HashMap<>();
    private Database desiredModel;
    private static ModelValidator modelClassValidator = new ModelValidator();
    private String tablePrefix;

    public void init(String tablePrefix, IDatabasePlatform platform, List<Class<?>> entityClasses, List<Class<?>> entityExtensionClasses) {
        this.platform = platform;
        this.tablePrefix = tablePrefix;
        this.entityClasses = entityClasses;
        this.entityExtensionClasses = entityExtensionClasses;
        desiredModel = buildDesiredModel();
    }

    protected Database buildDesiredModel() {
        Collection<Table> tables = loadTables(tablePrefix);
        loadExtensions();

        Database db = new Database();
        db.addTables(tables);
        platform.prefixDatabase(tablePrefix, db);
        for (Table table : db.getTables()) {
            String tableName = table.getName();
            if (tableName.endsWith("_")) {
                table.setName(tableName.substring(0, tableName.length() - 1));
            }
        }
        return db;
    }

    public Table getTable(Class<?> entityClass, Class<?> superClass) {
        List<ModelClassMetaData> metas = classToModelMetaData.get(entityClass).getModelClassMetaData();
        if (metas != null) {
            for (ModelClassMetaData modelMetaData : metas) {
                if (modelMetaData.getClazz().equals(superClass)) {
                    return modelMetaData.getTable();
                }
            }
        }
        return null;
    }

    public List<Table> getTables(Class<?> entityClass) {
        if (entityClass == null) {
            throw new PersistException("Cannot lookup a table for a null entity class.");
        }

        List<Table> tables = new ArrayList<>();
        List<ModelClassMetaData> metas = classToModelMetaData.get(entityClass).getModelClassMetaData();
        if (metas != null) {
            for (ModelClassMetaData ModelMetaData : metas) {
                tables.add(ModelMetaData.getTable());
            }
        }
        return tables;
    }

    protected void refreshMetaData(Database actualModel) {
        for (ModelMetaData modelClassTemplate : classToModelMetaData.values()) {
            for (Table actualTable : actualModel.getTables()) {
                for (ModelClassMetaData modelMetaData : modelClassTemplate.getModelClassMetaData()) {

                    Table desiredTable = modelMetaData.getTable();
                    if (desiredTable.getName().equalsIgnoreCase(actualTable.getName())) {
                        /*
                         * TODO causing invalid SQL generation right now on H2.
                         */
                        actualTable.setCatalog(null);
                        actualTable.setSchema(null);
                        modelMetaData.setTable(actualTable);
                        break;
                    }
                }
            }
        }
    }

    public boolean createAndUpgrade() {
        try {
            log.info("Checking if database tables need created or altered");

            platform.resetCachedTableModel();
            Database actualModel = platform.readFromDatabase(desiredModel.getTables());

            IDdlBuilder builder = platform.getDdlBuilder();

            String alterSql = builder.alterDatabase(actualModel, desiredModel, new SchemaObjectRemoveInterceptor());

            if (!StringUtils.isEmpty(alterSql)) {
                log.info("There are database tables that need to be created or altered. SQL generated:\r\n{}", alterSql);
                runScript(alterSql);
                actualModel = platform.readFromDatabase(desiredModel.getTables());
                log.info("Finished updating tables.");
                refreshMetaData(actualModel);
                return true;
            } else {
                refreshMetaData(actualModel);
                return false;
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void runScript(String alterSql) {
        String delimiter = platform.getDatabaseInfo().getSqlCommandDelimiter();
        SqlScript script = new SqlScript(alterSql, platform.getSqlTemplate(), true, false, false, delimiter, null);
        script.execute(platform.getDatabaseInfo().isRequiresAutoCommitForDdl());
    }

    protected Collection<Table> loadTables(String tablePrefix) {
        Set<Table> tables = new TreeSet<>();
        for (Class<?> entityClass : entityClasses) {
            //List<ModelClassMetaData> metas = createMetaDatas(entityClass);
            ModelMetaData modelMetaData = createMetaData(entityClass);
            classToModelMetaData.put(entityClass, modelMetaData);
            for (ModelClassMetaData meta : modelMetaData.getModelClassMetaData()) {
                validateTable(tablePrefix, meta.getTable());
                Table exists = tables.stream().filter(p -> p.equals(meta.getTable())).findFirst().orElse(null);
                if (exists != null) {
                    meta.setTable(exists);
                } else {
                    tables.add(meta.getTable());
                }
            }
        }
        return tables;
    }

    protected void loadExtensions() {
        for (Class<?> extensionClass : entityExtensionClasses) {
            Extends extendsAnnotation = extensionClass.getAnnotation(Extends.class);
            Class<?> baseClass = extendsAnnotation.entityClass();
            ModelMetaData modelMetaData = classToModelMetaData.get(baseClass);
            ModelClassMetaData meta = modelMetaData != null ? modelMetaData.getModelClassMetaData().get(0) : null;
            if (meta == null) {
                throw new PersistException("Failed to process extension entity " + extensionClass
                        + " Could not find table mapped for base entity class: " + baseClass);
            }
            Field[] fields = extensionClass.getDeclaredFields();
            for (Field field : fields) {
                meta.getTable().addColumn(createColumn(field));
            }
        }
    }
    
    protected String getTableName(String tablePrefix, String tableName) {
        if (isNotBlank(tablePrefix)) {
            return  tablePrefix + "_" + tableName;
        } else {
            return tableName;
        }
    }

    protected void validateTable(String tablePrefix, Table table) {
        String tableName = getTableName(tablePrefix, table.getName());
        validateName(tableName, "table", tableName);
        boolean hasPk = false;
        for (Column column : table.getColumns()) {
            validateName(column.getName(), "column", tableName);
            if (column.isPrimaryKey()) {
                hasPk = true;
            }
        }
        if (!hasPk) {
            throw new PersistException(
                    String.format("Table '%s' must define at least 1 primary key field with @Column(primaryKey=true)", tableName));
        }
    }

    protected void validateName(String nameToValidate, String type, String tableName) {
        final int ORACLE_MAX_NAME_LENGTH = 30;
        if (nameToValidate.length() == 0) {
            throw new PersistException(String.format("Invalid %s name \"%s\". The name cannot be blank.", type, nameToValidate));
        } else if (nameToValidate.length() > ORACLE_MAX_NAME_LENGTH) {
            throw new PersistException(String.format("Invalid %s name \"%s\". Must be 30 characters or less.", type, nameToValidate));
        } else if (ReservedWords.isReserved(nameToValidate)) {
            throw new PersistException(
                    String.format("Invalid %s name \"%s\" for table \"%s\". This is a reserved word. Try making the name more specific.",
                            type, nameToValidate, tableName));
        } else if (StringUtils.containsWhitespace(nameToValidate)) {
            throw new PersistException(String.format("Invalid %s name \"%s\".  The name contains whitespace.", type, nameToValidate));
        }
    }

    protected void extendTable(Table dbTable, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            dbTable.addColumn(createColumn(field));
        }
    }

    public static ModelMetaData createMetaData(Class<?> clazz) {
        List<ModelClassMetaData> list = new ArrayList<>();

        Class<?> entityClass = clazz;
        while (entityClass != null && entityClass != Object.class) {
            TableDef tblAnnotation = entityClass.getAnnotation(TableDef.class);
            if (tblAnnotation != null) {

                ModelClassMetaData meta = new ModelClassMetaData();
                meta.setClazz(entityClass);
                Table dbTable = new Table();
                List<Column> columns = new ArrayList<>();
                List<Column> pkColumns = new ArrayList<>();
                Map<String, IIndex> indices = new HashMap<>();
                dbTable.setName(tblAnnotation.name());
                dbTable.setDescription(tblAnnotation.description());
                Class<?> currentClass = entityClass;
                boolean includeAllFields = true;
                while (currentClass != null && currentClass != Object.class) {
                    Field[] fields = currentClass.getDeclaredFields();
                    for (Field field : fields) {
                        Column column = createColumn(field);
                        createIndex(field, column, indices);
                        if (column != null && (includeAllFields || column.isPrimaryKey())) {
                            if (isPrimaryKey(field)) {
                                meta.addEntityIdField(field.getName(), field);
                                pkColumns.add(0, column);
                            } else {
                                columns.add(column);
                            }
                            meta.addEntityField(field.getName(), field);
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                    includeAllFields = currentClass != null && currentClass.getAnnotation(TableDef.class) == null;
                }

                for (Column column : pkColumns) {
                    dbTable.addColumn(column);
                }

                for (Column column : columns) {
                    dbTable.addColumn(column);
                }

                for (IIndex index : indices.values()) {
                    dbTable.addIndex(index);
                }

                meta.setTable(dbTable);
                modelClassValidator.validate(meta);
                list.add(meta);

            }
            entityClass = entityClass.getSuperclass();
        }

        ModelMetaData metaData = new ModelMetaData();
        metaData.setModelClassMetaData(list);
        metaData.init();
        return metaData;
    }

    private static void createIndex(Field field, Column column, Map<String, IIndex> indices) {
        IndexDef colAnnotation = field.getAnnotation(IndexDef.class);
        if (colAnnotation != null) {
            String indexName = colAnnotation.name();
            boolean unique = colAnnotation.unique();
            indexName += (unique ? "_unq" : "");
            IIndex index = indices.get(indexName);
            if (index == null) {
                index = unique ? new UniqueIndex() : new NonUniqueIndex();
                index.setName(indexName);
                indices.put(indexName, index);
            }
            index.addColumn(new IndexColumn(column));
        }
    }

    private static boolean isPrimaryKey(Field field) {
        if (field != null) {
            ColumnDef colAnnotation = field.getAnnotation(ColumnDef.class);
            if (colAnnotation != null) {
                return colAnnotation.primaryKey();
            }
        }

        return false;
    }

    protected static Column createColumn(Field field) {
        Column dbCol = null;
        ColumnDef colAnnotation = field.getAnnotation(ColumnDef.class);
        if (colAnnotation != null) {
            dbCol = new Column();

            if (!StringUtils.isEmpty(colAnnotation.name())) {
                dbCol.setName(colAnnotation.name());
            } else {
                dbCol.setName(camelToSnakeCase(field.getName()));
            }

            dbCol.setDescription(colAnnotation.description());
            if (colAnnotation.type() == Types.OTHER) {
                dbCol.setTypeCode(getDefaultType(field));
            } else {
                dbCol.setTypeCode(colAnnotation.type());
            }
                       
            dbCol.setAutoIncrement(colAnnotation.autoIncrement());
            dbCol.setJdbcTypeName(getType(dbCol.getJdbcTypeCode()));
            
            if (isNotBlank(colAnnotation.defaultValue())) {
                dbCol.setDefaultValue(colAnnotation.defaultValue());
            }

            if (colAnnotation.size() != null & !colAnnotation.size().equalsIgnoreCase("")) {
                dbCol.setSize(colAnnotation.size());
            } else {
                dbCol.setSize(getDefaultSize(field, dbCol));
            }
            dbCol.setPrimaryKey(colAnnotation.primaryKey());

            if (colAnnotation.primaryKey()) {
                dbCol.setRequired(true);
            } else {
                dbCol.setRequired(colAnnotation.required());
            }
        }
        return dbCol;
    }

    public Map<String, String> getEntityIdColumnsToFields(Class<?> entityClass) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Map<String, String> entityIdColumnsToFields = new CaseInsensitiveMap();
        List<Field> fields = gettEntityIdFields(entityClass);
        for (Field field : fields) {
            ColumnDef colAnnotation = field.getAnnotation(ColumnDef.class);
            if (colAnnotation != null) {
                String columnName = null;
                if (!StringUtils.isEmpty(colAnnotation.name())) {
                    columnName = colAnnotation.name();
                } else {
                    columnName = camelToSnakeCase(field.getName());
                }
                entityIdColumnsToFields.put(columnName, field.getName());
            }
        }

        return entityIdColumnsToFields;
    }

    public Map<String, String> getColumnsToEntityFields(Class<?> entityClass) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Map<String, String> entityIdColumnsToFields = new CaseInsensitiveMap();
        List<Field> fields = getEntityFields(entityClass);
        for (Field field : fields) {
            org.jumpmind.pos.persist.ColumnDef colAnnotation = field.getAnnotation(org.jumpmind.pos.persist.ColumnDef.class);
            if (colAnnotation != null) {
                String columnName = null;
                if (!StringUtils.isEmpty(colAnnotation.name())) {
                    columnName = colAnnotation.name();
                } else {
                    columnName = camelToSnakeCase(field.getName());
                }
                entityIdColumnsToFields.put(columnName, field.getName());
            }
        }

        return entityIdColumnsToFields;
    }

    public Map<String, String> getEntityFieldsToColumns(Class<?> entityClass) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Map<String, String> entityFieldsToColumns = new CaseInsensitiveMap();
        List<Field> fields = getEntityFields(entityClass);
        for (Field field : fields) {
            org.jumpmind.pos.persist.ColumnDef colAnnotation = field.getAnnotation(org.jumpmind.pos.persist.ColumnDef.class);
            if (colAnnotation != null) {
                String columnName = null;
                if (!StringUtils.isEmpty(colAnnotation.name())) {
                    columnName = colAnnotation.name();
                } else {
                    columnName = camelToSnakeCase(field.getName());
                }
                entityFieldsToColumns.put(field.getName(), columnName);
            }
        }
        return entityFieldsToColumns;
    }

    protected List<Field> gettEntityIdFields(Class<?> entityClass) {
        ModelMetaData modelMetaData = classToModelMetaData.get(entityClass);
        ModelClassMetaData meta = modelMetaData != null ? modelMetaData.getModelClassMetaData().get(0) : null;
        return meta != null ? new ArrayList(meta.getEntityIdFields().values()) : Collections.emptyList();
    }

    protected List<Field> getEntityFields(Class<?> entityClass) {
        ModelMetaData modelMetaData = classToModelMetaData.get(entityClass);
        ModelClassMetaData meta = modelMetaData != null ? modelMetaData.getModelClassMetaData().get(0) : null;
        return meta != null ?  new ArrayList(meta.getEntityFields().values()) : Collections.emptyList();
    }

    protected static String getDefaultSize(Field field, Column column) {
        if (column.getMappedTypeCode() == Types.VARCHAR) {
            return "128";
        } else if (column.getJdbcTypeCode() == Types.DECIMAL) {
            return "12,3";
        }
        return null;
    }

    private static String getType(int type) {
        switch (type) {
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.BIGINT:
                return "BIGINT";
            case Types.INTEGER:
                return "INTEGER";
            case Types.BOOLEAN:
                return "BOOLEAN";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.DECIMAL:
                return "DECIMAL";
            case Types.BLOB:
                return "BLOB";
            default:
                return "OTHER";
        }
    }
    
    public String getTablePrefix() {
        return tablePrefix;
    }

    public ModelMetaData getModelMetaData(Class<?> modelClass) {
        return classToModelMetaData.get(modelClass);
    }

    // public ModelClassMetaData getModelMetaData(Class<?> entityClass, Table
    // table) {
    // List<ModelClassMetaData> metas =
    // for (ModelClassMetaData meta : metas) {
    // if (meta.getTable().equals(table)) {
    // return meta;
    // }
    // }
    //
    // return null;
    // }

    private static int getDefaultType(Field field) {
        if (field.getType().isAssignableFrom(String.class) || field.getType().isEnum() || ITypeCode.class.isAssignableFrom(field.getType())) {
            return Types.VARCHAR;
        } else if (field.getType().isAssignableFrom(long.class) || field.getType().isAssignableFrom(Long.class)) {
            return Types.BIGINT;
        } else if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)) {
            return Types.INTEGER;
        } else if (field.getType().isAssignableFrom(boolean.class) || field.getType().isAssignableFrom(Boolean.class)) {
            return Types.SMALLINT;
        } else if (field.getType().isAssignableFrom(Date.class)) {
            return Types.TIMESTAMP;
        } else if (field.getType().isAssignableFrom(BigDecimal.class) || field.getType().isAssignableFrom(Money.class)) {
            return Types.DECIMAL;
        } else if (field.getType().isAssignableFrom(byte[].class) || field.getType().isAssignableFrom(Byte[].class)) {
            return Types.BLOB;
        } else {
            return Types.OTHER;
        }
    }

    public static String camelToSnakeCase(String camelCase) {
        StringBuilder buff = new StringBuilder();
        int index = 0;
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c) && index > 0) {
                buff.append('_');
                buff.append(Character.toLowerCase(c));
            } else {
                buff.append(c);
            }
            index++;
        }

        return buff.toString().toLowerCase();
    }


}
