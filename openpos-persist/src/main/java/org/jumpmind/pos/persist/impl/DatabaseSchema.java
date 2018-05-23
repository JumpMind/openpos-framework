package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.IDdlBuilder;
import org.jumpmind.db.sql.SqlScript;
import org.jumpmind.pos.persist.Extends;
import org.jumpmind.pos.persist.PersistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatabaseSchema {

    protected static final Logger log = LoggerFactory.getLogger(DatabaseSchema.class);

    private IDatabasePlatform platform;
    private List<Class<?>> entityClasses;
    private List<Class<?>> entityExtensionClasses;
    private Map<Class<?>, EntityMetaData> classMetadata = new HashMap<>();
    private Database desiredModel;
    private Database actualModel;
    private String tablePrefix;

    public void init(String tablePrefix, IDatabasePlatform platform, List<Class<?>> entityClasses, List<Class<?>> entityExtensionClasses) {
        this.tablePrefix = tablePrefix;
        this.platform = platform;
        this.entityClasses = entityClasses;
        this.entityExtensionClasses = entityExtensionClasses;
        desiredModel = buildDesiredModel();
    }

    protected Database buildDesiredModel() {
        Database db = new Database();
        loadTables(db);
        loadExtensions(db);
        platform.prefixDatabase(tablePrefix, db);
        return db;
    }   
    
    
    public Table getTable(Class<?> entityClass) {
        if (entityClass == null) {
            throw new PersistException("Cannot lookup a table for a null entity class.");
        }
        
        EntityMetaData meta = classMetadata.get(entityClass);
        if (meta != null) {
            return meta.getTable();
        } else {
            return null;
        }
    }
    
    protected void refreshMetaData() {
        for (EntityMetaData entityMetaData : classMetadata.values()) {
            for (Table actualTable : actualModel.getTables()) {
                Table desiredTable = entityMetaData.getTable();
                if (desiredTable.getName().equalsIgnoreCase(actualTable.getName())) {
                    actualTable.setCatalog(null); // TODO causing invalid SQL generation right now on H2. 
                    actualTable.setSchema(null); // TODO
                    entityMetaData.setTable(actualTable);
                    break;
                }
            }
        }
    }
    
    public void modifyTable(Table desiredTable) {
        Table currentTable = platform.readTableFromDatabase(desiredTable.getCatalog(), desiredTable.getSchema(), desiredTable.getName());
        IDdlBuilder builder = platform.getDdlBuilder();
        String alterSql = builder.alterTable(currentTable, desiredTable);
        if (!StringUtils.isEmpty(alterSql)) {
            log.info("There are database tables that needed altered. SQL generated:\r\n{}", alterSql);
            runScript(alterSql);
        }
    }
    
    public boolean createAndUpgrade() {
        try {
            log.info("Checking if database tables need created or altered");
            
            platform.resetCachedTableModel();
            actualModel = platform.readFromDatabase(desiredModel.getTables());

            IDdlBuilder builder = platform.getDdlBuilder();
            
            String alterSql = builder.alterDatabase(actualModel, desiredModel, new SchemaObjectRemoveInterceptor());
            
            if (!StringUtils.isEmpty(alterSql)) {                    
                log.info("There are database tables that needed altered. SQL generated:\r\n{}", alterSql);
                runScript(alterSql);
                actualModel = platform.readFromDatabase(desiredModel.getTables());
                log.info("Finished updating tables.");
                refreshMetaData();
                return true;
            } else {
                refreshMetaData();
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

    protected void loadTables(Database db) {
        Map<Class<?>, EntityMetaData> classMetadataNew = new HashMap<>();
        for (Class<?> entityClass : entityClasses) {
            EntityMetaData meta = createMetaData(entityClass);
            validateTable(meta.getTable());
            classMetadataNew.put(entityClass, meta);
            db.addTable(meta.getTable());
        }
        this.classMetadata = classMetadataNew;
    }

    protected void loadExtensions(Database db) {
        for (Class<?> extensionClass : entityExtensionClasses) {
            Extends extendsAnnotation = extensionClass.getAnnotation(Extends.class);
            Class<?> baseClass = extendsAnnotation.entityClass();
            EntityMetaData meta = classMetadata.get(baseClass);
            if (meta == null) {
                throw new PersistException("Failed to process extension entity " + extensionClass + 
                        " Could not find table mapped for base entity class: " + baseClass);
            }
            Field[] fields = extensionClass.getDeclaredFields();
            for (Field field : fields) {
                meta.getTable().addColumn(createColumn(field));
            }                
        }
    }

    protected void validateTable(Table table) {
        String tableName = tablePrefix + "_" + table.getName();
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
        }
        else if (nameToValidate.length() > ORACLE_MAX_NAME_LENGTH) {
            throw new PersistException(String.format("Invalid %s name \"%s\". Must be 30 characeters or less.", type, nameToValidate));
        } else if (ReservedWords.isReserved(nameToValidate)) {
            throw new PersistException(String.format("Invalid %s name \"%s\" for table \"%s\". This is a reserved word. Try making the name more specific.", type, nameToValidate, tableName));
        } else if (StringUtils.containsWhitespace(nameToValidate)) {
            throw new PersistException(String.format("Invalid %s name \"%s\".  The name contains whitespace.", type, nameToValidate));
        }        
    }

    protected void extendTable(Table dbTable, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields) {
            dbTable.addColumn(createColumn(field));
        }               
    }

    protected EntityMetaData createMetaData(Class<?> entityClass) {
        
        EntityMetaData meta = new EntityMetaData();
        Table dbTable = new Table();
        org.jumpmind.pos.persist.Table tblAnnotation = entityClass.getAnnotation(org.jumpmind.pos.persist.Table.class);
        if (!StringUtils.isEmpty(tblAnnotation.name())) {            
            dbTable.setName(tblAnnotation.name());
        } else {
            dbTable.setName(camelToSnakeCase(entityClass.getSimpleName()));
        }
        dbTable.setDescription(tblAnnotation.description());        

        Class<?> currentClass = entityClass; 
        while (currentClass != null && currentClass != Object.class) {            
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                Column column = createColumn(field);
                if (column != null) {
                    dbTable.addColumn(column);
                    if (isPrimaryKey(field)) {
                        meta.getEntityIdFields().add(field);
                    }
                }
            }       
            currentClass = currentClass.getSuperclass();
        }
        
        meta.setTable(dbTable);
        return meta;
    }
    
    private boolean isPrimaryKey(Field field) {
        if (field != null) {            
            org.jumpmind.pos.persist.Column colAnnotation = field.getAnnotation(org.jumpmind.pos.persist.Column.class);
            if (colAnnotation != null) {
                return colAnnotation.primaryKey();
            }
        }
        
        return false;
    }    

    protected Column createColumn(Field field) {        
        Column dbCol = null;
        org.jumpmind.pos.persist.Column colAnnotation = field.getAnnotation(org.jumpmind.pos.persist.Column.class);
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
        Map<String, String> entityIdColumnsToFields = new LinkedHashMap<>();
        List<Field> fields = gettEntityIdFields(entityClass);
        for (Field field : fields) {
            org.jumpmind.pos.persist.Column colAnnotation = field.getAnnotation(org.jumpmind.pos.persist.Column.class);
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
    
    public List<Field> gettEntityIdFields(Class<?> entityClass) {
        EntityMetaData meta = classMetadata.get(entityClass);
        List<Field> fields = meta.getEntityIdFields();
        return fields;
    }

    protected String getDefaultSize(Field field, Column column) {
        if (column.getMappedTypeCode() == Types.VARCHAR) {
            return "128";
        } else if (column.getJdbcTypeCode() == Types.DECIMAL) {
            return "12";
        }
        return null;
    }
    
    private int getDefaultType(Field field) {
        if (field.getType().isAssignableFrom(String.class)) {
            return Types.VARCHAR;
        } else if (field.getType().isAssignableFrom(long.class)
                || field.getType().isAssignableFrom(Long.class)) {
            return Types.BIGINT;
        } else if (field.getType().isAssignableFrom(int.class)
                || field.getType().isAssignableFrom(Integer.class)) {
            return Types.INTEGER;
        } else if (field.getType().isAssignableFrom(boolean.class)
                || field.getType().isAssignableFrom(Boolean.class)) {
            return Types.BOOLEAN;
        } else if (field.getType().isAssignableFrom(Date.class)) {
            return Types.TIMESTAMP;
        } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
            return Types.DECIMAL;
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
    
    public Map<Class<?>, EntityMetaData> getClassMetadata() {
        return new HashMap<Class<?>, EntityMetaData>(classMetadata);
    }
    
    
    protected void validatePrimaryKey(Table table) {
        
    }    
}

