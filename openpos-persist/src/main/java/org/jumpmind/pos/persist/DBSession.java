package org.jumpmind.pos.persist;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;
import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.sql.DmlStatement;
import org.jumpmind.db.sql.DmlStatement.DmlType;
import org.jumpmind.db.sql.Row;
import org.jumpmind.pos.persist.impl.DatabaseSchema;
import org.jumpmind.pos.persist.impl.DefaultMapper;
import org.jumpmind.pos.persist.impl.DmlTemplate;
import org.jumpmind.pos.persist.impl.ModelClassMetaData;
import org.jumpmind.pos.persist.impl.ModelWrapper;
import org.jumpmind.pos.persist.impl.QueryTemplate;
import org.jumpmind.pos.persist.impl.ReflectUtils;
import org.jumpmind.pos.persist.model.ITaggedModel;
import org.jumpmind.pos.persist.model.SearchCriteria;
import org.jumpmind.pos.persist.model.TagHelper;
import org.jumpmind.pos.persist.model.TagModel;
import org.jumpmind.pos.util.model.ITypeCode;
import org.jumpmind.util.LinkedCaseInsensitiveMap;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBSession {

    private static final Logger log = Logger.getLogger(DBSession.class);

    private DatabaseSchema databaseSchema;
    private IDatabasePlatform databasePlatform;
    private JdbcTemplate jdbcTemplate;
    private Map<String, String> sessionContext;
    private Map<String, QueryTemplate> queryTemplates;
    private Map<String, DmlTemplate> dmlTemplates;
    private TagHelper tagHelper;

    public DBSession(String catalogName, String schemaName, DatabaseSchema databaseSchema, IDatabasePlatform databasePlatform,
            Map<String, String> sessionContext, Map<String, QueryTemplate> queryTemplates, Map<String, DmlTemplate> dmlTemplates,
            TagHelper tagHelper) {
        super();
        this.dmlTemplates = dmlTemplates;
        this.databaseSchema = databaseSchema;
        this.databasePlatform = databasePlatform;
        this.sessionContext = sessionContext;
        this.jdbcTemplate = new JdbcTemplate(databasePlatform.getDataSource());
        this.queryTemplates = queryTemplates;
        this.tagHelper = tagHelper;
    }

    public <T extends AbstractModel> List<T> findAll(Class<T> clazz) {
        Query<T> query = new Query<T>().result(clazz);
        return query(query, new HashMap<>());
    }

    public <T extends AbstractModel> T findByNaturalId(Class<T> entityClass, String id) {
        Map<String, String> naturalColumnsToFields = databaseSchema.getEntityIdColumnsToFields(entityClass);

        if (naturalColumnsToFields.size() != 1) {
            throw new PersistException(String.format(
                    "findByNaturalId cannot be used with a single 'id' " + "param because the entity defines %s natural key fields.",
                    naturalColumnsToFields.size()));
        }

        ModelId entityId = new ModelId() {
            public Map<String, Object> getIdFields() {
                Map<String, Object> fields = new HashMap<>(1);
                String fieldName = naturalColumnsToFields.values().iterator().next();
                fields.put(fieldName, id);
                return fields;
            }
        };

        return findByNaturalId(entityClass, entityId);
    }

    public Connection getConnection() {
        DataSource datasource = databasePlatform.getDataSource();
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            throw new PersistException("Failed to get connection from databasePlatform " + databasePlatform);
        }
    }

    public <T extends AbstractModel> T findByNaturalId(Class<T> entityClass, ModelId id) {
        QueryTemplate queryTemplate = new QueryTemplate();
        queryTemplate.setSelect(getSelectSql(entityClass, id.getIdFields()));
        Query<T> query = new Query<T>().result(entityClass);
        List<T> results = query(query, queryTemplate, id.getIdFields());

        if (results != null) {
            if (results.size() == 1) {
                return results.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This method will use any field on the entity that is set for search
     * criteria in the database. It will ignore all audit fields. Be sure to
     * null out any fields that might be set by default.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractModel> List<T> findByObjectDef(T entity) {
        Map<String, Object> fieldValues = toParamMap(entity, false);
        List<T> list = (List<T>) findByFields(entity.getClass(), fieldValues);
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractModel> List<T> findByCriteria(SearchCriteria searchCriteria) {
        if(searchCriteria.getEntityClass() == null) {
            throw new PersistException();
        }
        return (List<T>) findByFields(searchCriteria.getEntityClass(), searchCriteria.getCriteria());
    }

    public Map<String, Object> toParamMap(AbstractModel entity, boolean includeNullValues) {
        Map<String, Object> params = new HashMap<>();
        Class<?> clazz = entity.getClass();
        while (!clazz.equals(AbstractModel.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ColumnDef.class)) {
                    try {
                        Object value = field.get(entity);
                        if (includeNullValues || (value != null && !value.equals(""))) {
                            params.put(field.getName(), value);
                        }
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }

        return params;
    }

    public <T extends AbstractModel> List<T> findByFields(Class<T> entityClass, Map<String, Object> fieldValues) {
        QueryTemplate queryTemplate = new QueryTemplate();
        queryTemplate.setSelect(getSelectSql(entityClass, fieldValues));
        Query<T> query = new Query<T>().result(entityClass);
        return query(query, queryTemplate, fieldValues);
    }

    public void executeScript(File file) {
        try {
            executeScript(new FileReader(file));
        } catch (Exception ex) {
            if (ex instanceof PersistException) {
                throw (PersistException) ex;
            }
            throw new PersistException("Failed to execute script " + file, ex);
        }
    }

    public void executeScript(Reader reader) {
        try {
            RunScript.execute(getConnection(), reader);
        } catch (Exception ex) {
            throw new PersistException("Failed to execute script " + reader, ex);
        }
    }

    public int executeDml(String namedDml, Object... params) {
        DmlTemplate template = dmlTemplates.get(namedDml);
        if (template != null && isNotBlank(template.getDml())) {
            return jdbcTemplate.update(template.getDml(), params);
        } else {
            throw new PersistException("Could not find dml named: " + namedDml);
        }
    }

    public int executeSql(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    public <T> List<T> query(Query<T> query) {
        return query(query, (Map<String, Object>) null);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(Query<T> query, String singleParam) {
        QueryTemplate queryTemplate = getQueryTemplate(query);
        try {
            SqlStatement sqlStatement = queryTemplate.generateSQL(query, singleParam);
            return (List<T>) queryInternal(query.getResultClass(), sqlStatement);
        } catch (Exception ex) {
            throw new PersistException("Failed to query target class " + query.getResultClass(), ex);
        }
    }

    public <T> List<T> query(Query<T> query, Map<String, Object> params) {
        QueryTemplate queryTemplate = getQueryTemplate(query);
        return query(query, queryTemplate, params);
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Query<T> query, QueryTemplate queryTemplate, Map<String, Object> params) {
        try {
            SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
            return (List<T>) queryInternal(query.getResultClass(), sqlStatement);
        } catch (Exception ex) {
            throw new PersistException("Failed to query result class " + query.getResultClass(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> QueryTemplate getQueryTemplate(Query<T> query) {
        QueryTemplate queryTemplate = new QueryTemplate();
        boolean isEntityResult = AbstractModel.class.isAssignableFrom(query.getResultClass());
        // defined in config
        if (queryTemplates.containsKey(query.getName())) {
            queryTemplate = queryTemplates.get(query.getName()).copy();
        } else {            
            queryTemplate.setName(query.getName());
        }

        // generated from an Entity model.
        if (isEntityResult && queryTemplate.getSelect() == null) {
            Class<? extends AbstractModel> entityClass = (Class<? extends AbstractModel>) query.getResultClass();
            queryTemplate.setSelect(getSelectSql(entityClass, null));
        }
        return queryTemplate;
    }

    protected String getSelectSql(Class<?> entity, Map<String, Object> params) {
        List<Class<?>> toProcess = new ArrayList<>();
        Class<?> current = entity;
        /*
         * Add in reverse order because the superclass is going to have the
         * common denominator of primary keys
         */
        while (!current.equals(AbstractModel.class)) {
            if (current.isAnnotationPresent(TableDef.class)) {
                toProcess.add(0, current);
            }
            current = current.getSuperclass();
        }

        final String AND = " and ";
        final String JOIN = " join ";
        final String WHERE = " where ";
        final String COMMA = ", ";
        StringBuilder columns = new StringBuilder("select ");
        StringBuilder joins = new StringBuilder(" from ");
        StringBuilder where = new StringBuilder(WHERE);
        Set<String> columnsProcessed = new HashSet<>();
        int tableCount = 0;
        for (Class<?> processing : toProcess) {
            String tableAlias = "c" + tableCount;
            Table table = databaseSchema.getTable(entity, processing);
            if (table == null) {
                throw new PersistException("Cound not find table for the %s entity in the %s module.  Are you using the correct session?", entity.getSimpleName(), databaseSchema.getTablePrefix());
            }
            joins.append(table.getName()).append(" ").append(tableAlias);
            if (tableCount > 0) {
                String previousTableAlias = "c" + (tableCount - 1);
                Column[] pks = table.getPrimaryKeyColumns();
                joins.append(" on ");
                for (Column pk : pks) {
                    joins.append(previousTableAlias).append(".").append(pk.getName().toLowerCase()).append("=").append(tableAlias).append(".")
                            .append(pk.getName().toLowerCase()).append(AND);
                }
                joins.replace(joins.length() - AND.length(), joins.length(), "");
            }
            joins.append(JOIN);

            Map<String, String> columnToFieldMapping = databaseSchema.getColumnsToEntityFields(processing);
            for (Column column : table.getColumns()) {
                if (!columnsProcessed.contains(column.getName())) {
                    columns.append(tableAlias).append(".").append(column.getName().toLowerCase()).append(COMMA);

                    String fieldName = columnToFieldMapping.get(column.getName());
                    if (params != null && params.containsKey(fieldName)) {
                        where.append(tableAlias).append(".").append(column.getName().toLowerCase()).append("=${").append(fieldName)
                                .append("}").append(AND);
                    }

                    columnsProcessed.add(column.getName());
                }
            }
            tableCount++;

        }

        if (where.length() > WHERE.length()) {
            where.replace(where.length() - AND.length(), where.length(), "");
        } else {
            where.setLength(0);
        }
        columns.replace(columns.length() - COMMA.length(), columns.length(), "");
        joins.replace(joins.length() - JOIN.length(), joins.length(), "");

        return columns.toString() + joins.toString() + where.toString();

    }

    public void save(AbstractModel argModel) {
        List<Table> tables = getValidatedTables(argModel);

        ModelWrapper model = 
                new ModelWrapper(argModel, databaseSchema.getModelMetaData(argModel.getClass()));
        
        setMaintenanceValues(model);
        setTagValues(model);
        
        model.load();

        for (Table table : tables) {
            if (model.isNew()) {
                try {
                    insert(model, table);
                } catch (DuplicateKeyException ex) {
                    log.debug("Insert of entity failed, failing over to an update. " + argModel, ex);
                    int updateCount = update(model, table);
                    if (updateCount < 1) {
                        throw new PersistException("Failed to perform an insert or update on entity. Do the DB primary key and unique fields "
                                + "match what's understood by the code?  " + argModel, ex);
                    }
                }
            } else {
                if (update(model, table) == 0) {
                    insert(model, table);
                }
            }
        }
    }

    protected void insert(ModelWrapper model, Table table) {
        excecuteDml(DmlType.INSERT, model, table);
    }

    protected int update(ModelWrapper model, Table table) {
        return excecuteDml(DmlType.UPDATE, model, table);
    }

    protected int excecuteDml(DmlType type, ModelWrapper model, Table table) {
        boolean[] nullKeyValues = model.getNullKeys();
        List<Column> primaryKeyColumns = model.getPrimaryKeyColumns();

        DmlStatement statement = databasePlatform.createDmlStatement(type, table.getCatalog(), table.getSchema(), table.getName(),
                primaryKeyColumns.toArray(new Column[primaryKeyColumns.size()]), model.getColumns(table), nullKeyValues, null);
        String sql = statement.getSql();
        Object[] values = statement.getValueArray(model.getColumnNamesToValues());
        int[] types = statement.getTypes();

        return jdbcTemplate.update(sql, values, types);
    }

    protected List<Table> getValidatedTables(AbstractModel entity) {
        if (entity == null) {
            throw new PersistException("Failed to locate a database table for null entity class");
        }
        return getValidatedTables(entity.getClass());
    }

    protected List<Table> getValidatedTables(Class<?> entityClass) {
        List<Table> tables = databaseSchema.getTables(entityClass);
        if (tables == null || tables.size() == 0) {
            throw new PersistException("Failed to locate a database table for entity class: '" + entityClass
                    + "'. Make sure the correct dbSession is used with the module by using the correct @Qualifier");
        }
        return tables;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> queryInternal(Class<T> resultClass, SqlStatement statement) throws Exception {
        List<T> objects = new ArrayList<T>();
        List<Row> rows = jdbcTemplate.query(statement.getSql(), new DefaultMapper(), statement.getValues().toArray());

        for (int j = 0; j < rows.size(); j++) {
            Row row = rows.get(j);
            T object = null;

            if (resultClass.equals(String.class)) {
                object = (T) row.stringValue();
            } else if (isModel(resultClass)) {
                object = mapModel(resultClass, row);
            } else {
                object = mapNonModel(resultClass, row);
            }
            objects.add(object);
        }

        return objects;
    }

    protected boolean isModel(Class<?> resultClass) {
        return AbstractModel.class.isAssignableFrom(resultClass) && resultClass.getDeclaredAnnotation(TableDef.class) != null;
    }

    @SuppressWarnings("unchecked")
    protected <T> T mapModel(Class<T> resultClass, Row row) throws Exception {
        List<ModelClassMetaData> modelMetaData = databaseSchema.getModelMetaData(resultClass);

        T object = resultClass.newInstance();
        ModelWrapper model = new ModelWrapper((AbstractModel) object, modelMetaData);
        model.load();

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);

        LinkedCaseInsensitiveMap<String> matchedColumns = new LinkedCaseInsensitiveMap<String>();
        LinkedHashMap<String, Object> defferedLoadValues = new LinkedHashMap<>();

        for (int i = 0; i < propertyDescriptors.length; i++) {
            String propertyName = propertyDescriptors[i].getName();
            String columnName = DatabaseSchema.camelToSnakeCase(propertyName);

            if (row.containsKey(columnName)) {
                Object value = row.get(columnName);
                if (isDefferedLoadField(model.getField(propertyName))) {
                    defferedLoadValues.put(propertyName, value);
                } else {
                    model.setValue(propertyName, value);
                    matchedColumns.put(columnName, null);
                }
            }
        }

        for (String propertyName : defferedLoadValues.keySet()) {
            Object value = defferedLoadValues.get(propertyName);
            model.setValue(propertyName, value);
            String columnName = DatabaseSchema.camelToSnakeCase(propertyName);
            matchedColumns.put(columnName, null);
        }

        addTags(row, matchedColumns, (AbstractModel) object);
        addUnmatchedColumns(row, matchedColumns, (AbstractModel) object);
        decorateRetrievedModel(model);

        return (T) model.getModel();
    }

    protected void addTags(Row row, LinkedCaseInsensitiveMap<String> matchedColumns, AbstractModel model) {
        if (model instanceof ITaggedModel) {
            Map<String, Object> tagValues = new HashMap<>();
            for (String columnName : row.keySet()) {
                if (columnName.startsWith(TagModel.TAG_PREFIX)) {
                    matchedColumns.put(columnName, null);
                    tagValues.put(columnName, row.getString(columnName));
                }
            }
            tagHelper.addTags((ITaggedModel)model, tagValues);
        }
    }

    protected boolean isDefferedLoadField(Field field) {
        return field.getType().isAssignableFrom(Money.class) || ITypeCode.class.isAssignableFrom(field.getClass());
    }

    protected <T> T mapNonModel(Class<T> resultClass, Row row) throws Exception {
        T object = resultClass.newInstance();
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);

        LinkedCaseInsensitiveMap<String> matchedColumns = new LinkedCaseInsensitiveMap<String>();

        for (int i = 0; i < propertyDescriptors.length; i++) {
            String propertyName = propertyDescriptors[i].getName();
            String columnName = DatabaseSchema.camelToSnakeCase(propertyName);

            if (row.containsKey(columnName)) {
                Object value = row.get(columnName);
                ReflectUtils.setProperty(object, propertyName, value);
                matchedColumns.put(columnName, null);
            }
        }
        return object;
    }

    protected void addUnmatchedColumns(Row row, Map<String, String> matchedColumns, AbstractModel entity) {
        for (String rowColumn : row.keySet()) {
            if (!matchedColumns.containsKey(rowColumn)) {
                entity.setAdditionalField(rowColumn, row.get(rowColumn));
            }
        }
    }

    protected void decorateRetrievedModel(ModelWrapper model) {
        model.setRetrievalTime(new Date());
    }

    public void saveAll(List<? extends AbstractModel> entities) {
        for (AbstractModel entity : entities) {
            save(entity);
        }
    }

    public void close() {
        try {
            getConnection().close();
        } catch (Exception ex) {
            log.debug("Failed to close connection", ex);
        }
    }
    
    protected void setTagValues(ModelWrapper model) {
        if (model.getModel() instanceof ITaggedModel) {
            ITaggedModel taggedModel = (ITaggedModel) model.getModel();
            for (TagModel tag : tagHelper.getTagConfig().getTags()) {
                String name = tag.getName().toUpperCase();
                if (StringUtils.isEmpty(taggedModel.getTagValue(name))) {
                    taggedModel.setTagValue(name, "*");
                }
            }
        }
    }

    protected void setMaintenanceValues(ModelWrapper model) {
        if (StringUtils.isEmpty(model.getCreateBy())) {
            model.setCreateBy(sessionContext.get("CREATE_BY"));
        }
        if (StringUtils.isEmpty(model.getLastUpdateBy())) {
            model.setLastUpdateBy(sessionContext.get("LAST_UPDATE_BY"));
        }
    }    
}
