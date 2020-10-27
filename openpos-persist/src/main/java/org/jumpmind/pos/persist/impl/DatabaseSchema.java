package org.jumpmind.pos.persist.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.IIndex;
import org.jumpmind.db.model.IndexColumn;
import org.jumpmind.db.model.NonUniqueIndex;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.model.UniqueIndex;
import org.jumpmind.db.platform.DatabaseNamesConstants;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.IDdlBuilder;
import org.jumpmind.db.sql.SqlScript;
import org.jumpmind.pos.persist.*;
import org.jumpmind.pos.persist.model.AugmenterConfig;
import org.jumpmind.pos.persist.model.AugmenterHelper;
import org.jumpmind.pos.persist.model.AugmenterIndexConfig;
import org.jumpmind.pos.persist.model.AugmenterModel;
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
    private AugmenterHelper augmenterHelper;

    @SneakyThrows
    public void init(String tablePrefix, IDatabasePlatform platform, List<Class<?>> entityClasses, List<Class<?>> entityExtensionClasses, AugmenterHelper augmenterHelper) {
        this.platform = platform;
        this.tablePrefix = tablePrefix;
        this.entityClasses = entityClasses;
        this.entityExtensionClasses = entityExtensionClasses;
        this.augmenterHelper = augmenterHelper;
        desiredModel = buildDesiredModel();
    }

    protected Database buildDesiredModel() {
        Collection<Table> tables = loadTables(tablePrefix);

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
            ModelMetaData modelMetaData = createMetaData(entityClass, entityExtensionClasses, platform);
            classToModelMetaData.put(entityClass, modelMetaData);
            for (ModelClassMetaData meta : modelMetaData.getModelClassMetaData()) {
                validateTable(tablePrefix, meta.getTable());
                Table exists = tables.stream().filter(p -> p.getName().equals(meta.getTable().getName())).findFirst().orElse(null);
                if (exists != null) {
                    meta.setTable(exists);
                } else {
                    tables.add(meta.getTable());
                }
            }
        }
        return tables;
    }

    protected static List<Class<?>> getEntityExtensionClasses(Class<?> entityClazz, List<Class<?>> entityExtensionClasses) {
        if (entityExtensionClasses != null) {
            return entityExtensionClasses.stream().filter(extensionClazz -> {
                Extends extendsAnnotation = extensionClazz.getAnnotation(Extends.class);
                return extendsAnnotation != null && entityClazz.equals(extendsAnnotation.entityClass());
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    protected String getTableName(String tablePrefix, String tableName) {
        if (isNotBlank(tablePrefix)) {
            return tablePrefix + "_" + tableName;
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
                    String.format("Table '%s' must define at least 1 primary key field in @Table(primaryKey=\"fieldName\")", tableName));
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

    public ModelMetaData createMetaData(Class<?> clazz, List<Class<?>> entityExtensionClasses) {
        return createMetaData(clazz, entityExtensionClasses, null);
    }

    @SneakyThrows
    public ModelMetaData createMetaData(Class<?> clazz, List<Class<?>> entityExtensionClasses, IDatabasePlatform databasePlatform) {
        List<ModelClassMetaData> list = new ArrayList<>();
        Class<?> entityClass = clazz;
        boolean ignoreSuperClasses = false;
        while (entityClass != null && entityClass != Object.class) {
            List<Class<?>> myExtensions = getEntityExtensionClasses(entityClass, entityExtensionClasses);
            TableDef tblAnnotation = entityClass.getAnnotation(TableDef.class);
            if (tblAnnotation != null && !ignoreSuperClasses) {
                ignoreSuperClasses = tblAnnotation.ignoreSuperTableDef();
                ModelClassMetaData meta = new ModelClassMetaData();
                meta.setClazz(entityClass);
                meta.setExtensionClazzes(myExtensions);
                meta.setPrimaryKeyFieldNames(getPrimaryKeyNames(tblAnnotation));

                Table dbTable = new Table();
                List<Column> columns = new ArrayList<>();
                dbTable.setName(tblAnnotation.name());
                dbTable.setDescription(tblAnnotation.description());
                Class<?> currentClass = entityClass;
                boolean includeAllFields = true;
                while (currentClass != null && currentClass != Object.class) {
                    createClassFieldsMetadata(currentClass, meta, includeAllFields, columns, databasePlatform);
                    currentClass = currentClass.getSuperclass();
                    includeAllFields = currentClass != null && (currentClass.getAnnotation(TableDef.class) == null || ignoreSuperClasses);
                }
                for (Class<?> extensionClass : myExtensions) {
                    createClassFieldsMetadata(extensionClass, meta, true, columns, databasePlatform);
                }
                createAugmentedFieldsMetaData(meta, columns, databasePlatform);
                meta.init();

                for (Column column : meta.getPrimaryKeyColumns()) {
                    dbTable.addColumn(column);
                }
                for (Column column : columns) {
                    dbTable.addColumn(column);
                }

                meta.setTable(dbTable);
                modelClassValidator.validate(meta);
                list.add(meta);
            }
            entityClass = entityClass.getSuperclass();
        }

        for (ModelClassMetaData meta: list) {
            Class<?> currentClass = meta.getClazz();
            Table dbTable = meta.getTable();
            IndexDefs indexDefs = currentClass.getAnnotation(IndexDefs.class);
            Map<String, IIndex> indices = createIndices(indexDefs, dbTable, meta, databasePlatform);
            for (IIndex index : indices.values()) {
                dbTable.addIndex(index);
            }
            Map<String, IIndex> augmentedIndices = createAugmentedIndices(currentClass, dbTable, meta, platform);
            for (IIndex index : augmentedIndices.values()) {
                dbTable.addIndex(index);
            }
        }

        ModelMetaData metaData = new ModelMetaData();
        metaData.setModelClassMetaData(list);
        metaData.init();
        return metaData;
    }

    private void createAugmentedFieldsMetaData(ModelClassMetaData meta, List<Column> columns, IDatabasePlatform databasePlatform) {
        List<AugmenterConfig> configs = augmenterHelper.getAugmenterConfigs(meta.getClazz());
        for (AugmenterConfig config : configs) {
            meta.getAugmenterConfigs().add(config);
            for (AugmenterModel augmenterModel : config.getAugmenters()) {
                meta.getAugmentedFieldNames().add(augmenterModel.getName());
                Column column = new Column();
                column.setName(alterCaseToMatchDatabaseDefaultCase(config.getPrefix(), databasePlatform) + alterCaseToMatchDatabaseDefaultCase(camelToSnakeCase(augmenterModel.getName()), databasePlatform));
                column.setDefaultValue(augmenterModel.getDefaultValue());
                column.setSize(augmenterModel.getSize() != null ? Integer.toString(augmenterModel.getSize()) : "32");
                column.setTypeCode(Types.VARCHAR);
                columns.add(column);
            }
        }
    }

    public static Set<String> getPrimaryKeyNames(TableDef tblAnnotation) {
        Set<String> pks = new LinkedHashSet<>();

        for (String element : tblAnnotation.primaryKey()) {
            pks.addAll(getPrimaryKeyNames(element));
        }

        return pks;
    }

    private static Set<String> getPrimaryKeyNames(String element) {
        String[] pkFieldNames = element.split("\\,");
        Set<String> pks = new LinkedHashSet<>();
        for (String pkFieldName : pkFieldNames) {
            pkFieldName = pkFieldName.trim();
            if (!StringUtils.isEmpty(pkFieldName)) {
                pks.add(pkFieldName);
            }
        }

        return pks;
    }

    @SneakyThrows
    private void createClassFieldsMetadata(Class<?> clazz, ModelClassMetaData metaData,
                                                  boolean includeAllFields, List<Column> columns, IDatabasePlatform platform) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Column column = createColumn(field, platform, metaData);
            if (column != null && (includeAllFields || column.isPrimaryKey())) {
                if (isPrimaryKey(field, metaData)) {
                    metaData.addEntityIdFieldMetadata(field.getName(), new FieldMetaData(clazz, field, column));
                } else {
                    columns.add(column);
                }
                FieldMetaData fieldMetaData = new FieldMetaData(clazz, field, column);
                metaData.addEntityFieldMetaData(field.getName(), fieldMetaData);
                ColumnDef colAnnotation = field.getAnnotation(ColumnDef.class);
                if (colAnnotation != null && ArrayUtils.isNotEmpty(colAnnotation.propertyAliases())) {
                    for (String alias: colAnnotation.propertyAliases()) {
                        // Add a mapping entry for each optional propertyAlias for the column
                        metaData.addEntityFieldMetaData(alias, fieldMetaData);
                    }
                }
            }
            CompositeDef compositeDefAnnotation = field.getAnnotation(CompositeDef.class);
            if (compositeDefAnnotation != null) {
                metaData.setIdxPrefix(compositeDefAnnotation.prefix());
                createClassFieldsMetadata(field.getType(), metaData, includeAllFields, columns, platform);
            }
        }
    }

    private Map<String, IIndex> createIndices(IndexDefs indexDefs, Table table,
                                                     ModelClassMetaData metaData, IDatabasePlatform platform) {
        Map<String, IIndex> indices = new HashMap<>();
        if (indexDefs != null && indexDefs.value() != null) {
            for (IndexDef indexDef : indexDefs.value()) {
                parseIndexDef(indexDef, table, indices, metaData, platform);
            }
        }

        return indices;
    }

    private void parseIndexDef(IndexDef indexDef, Table table, Map<String, IIndex> indices, ModelClassMetaData metaData, IDatabasePlatform platform) {
        if (indexDef.column() != null && !indexDef.column().isEmpty()) {
            Column column = findColumn(indexDef.column(), table, platform);
            createIndex(indexDef, column, indexDef.column(), indices, metaData.getIdxPrefix());
        } else if (indexDef.columns() != null && indexDef.columns().length > 0) {
            for (String columnName : indexDef.columns()) {
                Column column = findColumn(columnName, table, platform);
                createIndex(indexDef, column, columnName, indices, metaData.getIdxPrefix());
            }
        } else {
            log.warn("Unable to create index '{}', no columns found in definition", indexDef.name());
        }
    }

    private Column findColumn(String columnName, String augmentedPrefix, Table table, IDatabasePlatform platform) {
        String augmentedColumnName = alterCaseToMatchDatabaseDefaultCase(augmentedPrefix + camelToSnakeCase(columnName), platform);
        return table.getColumnWithName(augmentedColumnName);
    }

    private Column findColumn(String columnName, Table table, IDatabasePlatform platform) {
        String snakeCase = alterCaseToMatchDatabaseDefaultCase(camelToSnakeCase(columnName), platform);
        Column column = table.getColumnWithName(snakeCase);
        return column;
    }

    private void createIndex(IndexDef indexDef, Column column, String columnName, Map<String, IIndex> indices, String idxPrefix) {
        if (column != null && indexDef != null) {
            String indexName = idxPrefix != null && !idxPrefix.isEmpty() ? idxPrefix + "_" + indexDef.name() : indexDef.name();
            boolean unique = indexDef.unique();
            indexName += (unique ? "_unq" : "");
            IIndex index = indices.get(indexName);
            if (index == null) {
                index = unique ? new UniqueIndex() : new NonUniqueIndex();
                index.setName(indexName);
                indices.put(indexName, index);
            }
            index.addColumn(new IndexColumn(column));
        } else {
            log.warn("Unable to create index for column '{}', unable to find column on table", columnName);
        }
    }

    private boolean isPrimaryKey(Field field, ModelClassMetaData metaData) {
        if (field != null) {
            ColumnDef colAnnotation = field.getAnnotation(ColumnDef.class);
            if (colAnnotation != null) {
                return (metaData.getPrimaryKeyFieldNames().contains(field.getName()));
            }
        }

        return false;
    }

    private String alterCaseToMatchDatabaseDefaultCase(String name, IDatabasePlatform platform) {
        if (platform != null) {
            name = platform.alterCaseToMatchDatabaseDefaultCase(name);
        }
        return name;
    }

    private Column createColumn(Field field, IDatabasePlatform platform, ModelClassMetaData metaData) {
        Column dbCol = null;
        ColumnDef colAnnotation = field.getAnnotation(ColumnDef.class);


        if (colAnnotation != null) {
            dbCol = new Column();

            if (!StringUtils.isEmpty(colAnnotation.name())) {
                dbCol.setName(alterCaseToMatchDatabaseDefaultCase(colAnnotation.name(), platform));
            } else {
                dbCol.setName(alterCaseToMatchDatabaseDefaultCase(camelToSnakeCase(field.getName()), platform));
            }

            dbCol.setDescription(colAnnotation.description());
            if (colAnnotation.type() == Types.OTHER) {
                dbCol.setTypeCode(getDefaultType(field));
            } else if (colAnnotation.type() == Types.CLOB && platformMatches(DatabaseNamesConstants.ORACLE, platform)) {
                dbCol.setTypeCode(Types.LONGVARCHAR);
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
            dbCol.setPrimaryKey(metaData.isPrimaryKey(field));

            if (metaData.isPrimaryKey(field)) {
                dbCol.setRequired(true);
            } else {
                dbCol.setRequired(colAnnotation.required());
            }
        }
        return dbCol;
    }

    private boolean platformMatches(String name, IDatabasePlatform platform) {
        return platform != null && name != null && platform.getName().equals(name);
    }

    public Map<String, String> getEntityIdColumnsToFields(Class<?> entityClass) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Map<String, String> entityIdColumnsToFields = new CaseInsensitiveMap();
        List<FieldMetaData> fields = getEntityIdFields(entityClass);
        for (FieldMetaData fieldMetaData : fields) {
            Field field = fieldMetaData.getField();
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
        @SuppressWarnings({"rawtypes", "unchecked"})
        Map<String, String> entityIdColumnsToFields = new CaseInsensitiveMap();
        List<FieldMetaData> fields = getEntityFields(entityClass);
        for (FieldMetaData field : fields) {
            org.jumpmind.pos.persist.ColumnDef colAnnotation = field.getField().getAnnotation(org.jumpmind.pos.persist.ColumnDef.class);
            if (colAnnotation != null) {
                String columnName = null;
                if (!StringUtils.isEmpty(colAnnotation.name())) {
                    columnName = colAnnotation.name();
                } else {
                    columnName = camelToSnakeCase(field.getField().getName());
                }
                entityIdColumnsToFields.put(columnName, field.getField().getName());
            }
        }
        Map<String, String> augmentedColumnsToFields = getAugmentedColumnsToFields(entityClass);
        Map<String, String> mergedMaps = Stream.concat(entityIdColumnsToFields.entrySet().stream(), augmentedColumnsToFields.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1, value2) -> value1,
                        CaseInsensitiveMap::new));
        return mergedMaps;
    }

    private Map<String, String> getAugmentedColumnsToFields(Class<?> entityClass) {
        Map<String, String> columnsToAugmentedFields = new CaseInsensitiveMap<>();
        List<AugmenterConfig> configs = augmenterHelper.getAugmenterConfigs(entityClass);
        for (AugmenterConfig config : configs) {
            for (String augmenterName: config.getAugmenterNames()) {
                columnsToAugmentedFields.put(config.getPrefix() + camelToSnakeCase(augmenterName), augmenterName);
            }
        }
        return columnsToAugmentedFields;
    }

    public Map<String, String> getEntityFieldsToColumns(Class<?> entityClass) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Map<String, String> entityFieldsToColumns = new CaseInsensitiveMap();
        List<FieldMetaData> fields = getEntityFields(entityClass);
        for (FieldMetaData field : fields) {
            org.jumpmind.pos.persist.ColumnDef colAnnotation = field.getField().getAnnotation(org.jumpmind.pos.persist.ColumnDef.class);
            if (colAnnotation != null) {
                String columnName = null;
                if (!StringUtils.isEmpty(colAnnotation.name())) {
                    columnName = colAnnotation.name();
                } else {
                    columnName = camelToSnakeCase(field.getField().getName());
                }
                entityFieldsToColumns.put(field.getField().getName(), columnName);
            }
        }
        return entityFieldsToColumns;
    }

    protected List<FieldMetaData> getEntityIdFields(Class<?> entityClass) {
        ModelMetaData modelMetaData = classToModelMetaData.get(entityClass);
        ModelClassMetaData meta = modelMetaData != null ? modelMetaData.getModelClassMetaData().get(0) : null;
        return meta != null ? new ArrayList(meta.getEntityIdFieldMetaDatas().values()) : Collections.emptyList();
    }

    protected List<FieldMetaData> getEntityFields(Class<?> entityClass) {
        ModelMetaData modelMetaData = classToModelMetaData.get(entityClass);
        ModelClassMetaData meta = modelMetaData != null ? modelMetaData.getModelClassMetaData().get(0) : null;
        return meta != null ? new ArrayList(meta.getEntityFieldMetaDatas().values()) : Collections.emptyList();
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

    private int getDefaultType(Field field) {
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

    private Map<String, IIndex> createAugmentedIndices(Class<?> currentClass, Table table,
                                                       ModelClassMetaData metaData, IDatabasePlatform platform) {
        Map<String, IIndex> indices = new HashMap<>();
        Augmented augmented = currentClass.getAnnotation(Augmented.class);
        if (augmented != null) {
            AugmenterConfig config = augmenterHelper.getAugmenterConfig(augmented.name());
            if (config != null && config.getIndexConfigs() != null) {
                List<AugmenterIndexConfig> indexConfigs = config.getIndexConfigs();
                for (AugmenterIndexConfig indexConfig : indexConfigs) {
                    createIndex(indexConfig, table, indices, augmented.indexPrefix(), platform, config.getPrefix());
                }
            }
        }
        return indices;
    }

    private void createIndex(AugmenterIndexConfig indexConfig, Table table, Map<String, IIndex> indices, String idxPrefix, IDatabasePlatform platform, String augmentedColumnPrefix) {
        String indexName = idxPrefix != null && !idxPrefix.isEmpty() ? idxPrefix + "_" + indexConfig.getName() : indexConfig.getName();
        indexName += (indexConfig.isUnique() ? "_unq" : "");
        IIndex index = indices.get(indexName);
        if (index == null) {
            index = indexConfig.isUnique() ? new UniqueIndex(indexName) : new NonUniqueIndex(indexName);
            indices.put(indexName, index);
        }
        for (String columnName : indexConfig.getColumnNames()) {
            Column column = findColumn(columnName, augmentedColumnPrefix, table, platform);
            if (column == null) {
                column = findColumn(columnName, table, platform);
            }
            if (column != null) {
                index.addColumn(new IndexColumn(column));
            }
            if (column == null) {
                log.warn("No column {} found for index {} on augmented table {}", columnName, indexName, table.getName());
            }
        }
    }


}
