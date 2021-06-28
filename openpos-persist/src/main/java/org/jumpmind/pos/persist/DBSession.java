package org.jumpmind.pos.persist;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;
import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.sql.DmlStatement;
import org.jumpmind.db.sql.DmlStatement.DmlType;
import org.jumpmind.db.sql.LogSqlBuilder;
import org.jumpmind.db.sql.Row;
import org.jumpmind.pos.persist.impl.*;
import org.jumpmind.pos.persist.model.*;
import org.jumpmind.pos.util.ReflectUtils;
import org.jumpmind.pos.util.model.ITypeCode;
import org.jumpmind.properties.TypedProperties;
import org.jumpmind.util.LinkedCaseInsensitiveMap;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
public class DBSession {

    public static final String JDBC_QUERY_TIMEOUT = "openpos.jdbc.queryTimeoutSec";
    public static final String JDBC_FETCH_SIZE = "openpos.jdbc.fetchSize";

    private DatabaseSchema databaseSchema;
    @Getter
    private IDatabasePlatform databasePlatform;
    private TypedProperties sessionContext;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private QueryTemplates queryTemplates;
    private DmlTemplates dmlTemplates;
    private TagHelper tagHelper;
    private AugmenterHelper augmenterHelper;

    public DBSession(String catalogName, String schemaName, DatabaseSchema databaseSchema, IDatabasePlatform databasePlatform,
                     TypedProperties sessionContext, QueryTemplates queryTemplates, DmlTemplates dmlTemplates,
                     TagHelper tagHelper, AugmenterHelper augmenterHelper) {
        super();
        this.dmlTemplates = dmlTemplates;
        this.databaseSchema = databaseSchema;
        this.databasePlatform = databasePlatform;
        this.sessionContext = sessionContext;
        JdbcTemplate baseTemplate = new JdbcTemplate(databasePlatform.getDataSource());
        baseTemplate.setQueryTimeout(sessionContext.getInt(JDBC_QUERY_TIMEOUT, 10));
        baseTemplate.setFetchSize(sessionContext.getInt(JDBC_FETCH_SIZE, 1000));
        this.jdbcTemplate = new NamedParameterJdbcTemplate(baseTemplate);
        this.queryTemplates = queryTemplates;
        this.tagHelper = tagHelper;
        this.augmenterHelper = augmenterHelper;
    }

    public <T extends AbstractModel> List<T> findAll(Class<T> clazz, int maxResults) {
        Query<T> query = new Query<T>().result(clazz);
        return query(query, new HashMap<>(), maxResults);
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
            throw new PersistException("Failed to get connection from databasePlatform " + databasePlatform, e);
        }
    }

    public <T extends AbstractModel> T findByNaturalId(Class<T> entityClass, ModelId id) {
        QueryTemplate queryTemplate = new QueryTemplate();
        queryTemplate.setSelect(getSelectSql(entityClass, id.getIdFields()));
        Query<T> query = new Query<T>().result(entityClass);
        List<T> results = query(query, queryTemplate, id.getIdFields(), 1);

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
    public <T extends AbstractModel> List<T> findByObjectDef(T entity, int maxResults) {
        Map<String, Object> fieldValues = toParamMap(entity, false);
        List<T> list = (List<T>) findByFields(entity.getClass(), fieldValues, maxResults);
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractModel> List<T> findByCriteria(SearchCriteria searchCriteria) {
        if (searchCriteria.getEntityClass() == null) {
            throw new PersistException();
        }
        return (List<T>) findByFields(searchCriteria.getEntityClass(), searchCriteria.getCriteria(), searchCriteria.getMaxResults());
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

    public <T extends AbstractModel> T findFirstByFields(Class<T> entityClass, Map<String, Object> fieldValues, int maxResults) {
        List<T> list = findByFields(entityClass, fieldValues, maxResults);
        return list.size() > 0 ? list.get(0) : null;
    }

    public <T extends AbstractModel> List<T> findByFields(Class<T> entityClass, Map<String, Object> fieldValues, int maxResults) {
        QueryTemplate queryTemplate = new QueryTemplate();
        queryTemplate.setSelect(getSelectSql(entityClass, fieldValues));
        Query<T> query = new Query<T>().result(entityClass);
        return query(query, queryTemplate, fieldValues, maxResults);
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
        try (Connection connection = getConnection()) {
            RunScript.execute(connection, reader);
        } catch (Exception ex) {
            throw new PersistException("Failed to execute script " + reader, ex);
        }
    }

    public int executeDml(String namedDml, Map<String, Object> params) {
        DmlTemplate template = this.getDmlTemplate(namedDml);
        if (template != null && isNotBlank(template.getDml())) {
            SqlStatement statement = template.generateSQL(template.getDml(), params);
            ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(statement.getSql());
            String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, statement.getParameters());
            List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, statement.getParameters());
            Object[] args = NamedParameterUtils.buildValueArray(parsedSql, statement.getParameters(), declaredParameters);

            PreparedStatementCreator psc = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters).newPreparedStatementCreator(args);
            return jdbcTemplate.getJdbcOperations().update(psc);
        } else {
            throw new PersistException("Could not find dml named: " + namedDml);
        }
    }

    @Deprecated
    public int executeDml(String namedDml, Object... params) {
        DmlTemplate template = this.getDmlTemplate(namedDml);
        if (template != null && isNotBlank(template.getDml())) {
            params = Arrays.stream(params).
                    map(p -> p instanceof Boolean ? (((Boolean) p ? 1 : 0)) : p).
                    collect(Collectors.toList()).
                    toArray(new Object[params.length]);
            return jdbcTemplate.getJdbcOperations().update(template.getDml(), params);
        } else {
            throw new PersistException("Could not find dml named: " + namedDml);
        }
    }

    public int[] executeBatchSql(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.getJdbcOperations().batchUpdate(sql, batchArgs);
    }

    public int[] executeBatchSql(String sql, List<Object[]> batchArgs, int[] argTypes) {
        return jdbcTemplate.getJdbcOperations().batchUpdate(sql, batchArgs, argTypes);
    }


    public int executeSql(String sql, Object... params) {
        return jdbcTemplate.getJdbcOperations().update(sql, params);
    }

    public <T> List<T> query(Query<T> query) {
        return query(query, (Map<String, Object>) null, 100);
    }

    public <T> List<T> query(Query<T> query, int maxResults) {
        return query(query, (Map<String, Object>) null, maxResults);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(Query<T> query, Object singleParam, int maxResults) {
        QueryTemplate queryTemplate = getQueryTemplate(query);
        try {
            SqlStatement sqlStatement = queryTemplate.generateSQL(query, singleParam);
            return (List<T>) queryInternal(query.getResultClass(), sqlStatement, maxResults);
        } catch (Exception ex) {
            throw new PersistException("Failed to query target class " + query.getResultClass(), ex);
        }
    }

    public <T> List<T> query(Query<T> query, Map<String, Object> params, int maxResults) {
        QueryTemplate queryTemplate = getQueryTemplate(query);
        return query(query, queryTemplate, params, maxResults);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(String sqlQuery, Map<String, Object> params) {
        try {
            QueryTemplate queryTemplate = new QueryTemplate();
            queryTemplate.setSelect(sqlQuery);

            Query query = new Query<>().named("ANONYMOUS");

            SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
            return (List<T>) queryInternal(null, sqlStatement, 10000);
        } catch (Exception ex) {
            throw new PersistException("Failed to run query: " + sqlQuery, ex);
        }
    }

    public Integer queryForInt(Query query, Map<String, Object> params) {
        QueryTemplate queryTemplate = getQueryTemplate(query);
        List results = null;
        try {
            SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
            results = queryInternal(null, sqlStatement, 1000);

            if (results.size() == 1) {
                Row row = (Row) results.get(0);

                if (row.keySet().size() == 1) {
                    Object value = row.values().iterator().next();

                    if (value instanceof Long) {
                        int intValue = Math.toIntExact(((Long) value).longValue());
                        return new Integer(intValue);

                    } else if (value instanceof Integer) {
                        return (Integer) value;

                    } else {
                        throw new PersistException("Unexpected result: " + value);
                    }
                }
            }
        } catch (Exception ex) {
            throw new PersistException("Failed to execute query. Name: " + query.getName() + " Parameters: " + params, ex);
        }
        throw new PersistException("Invalid results: Failed to execute query. Name: " + query.getName() + " Parameters: " + params + " Results: " + results);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(Query<T> query, QueryTemplate queryTemplate, Map<String, Object> params, int maxResults) {
        SqlStatement sqlStatement;

        try {
            sqlStatement = queryTemplate.generateSQL(query, params);
            return (List<T>) queryInternal(query.getResultClass(), sqlStatement, maxResults);
        } catch (Exception ex) {
            throw new PersistException("Failed to execute query. Name: " + query.getName() + " result class: " +
                    query.getResultClass() + " Parameters: " + params, ex);
        }
    }

    public ModelWrapper wrap(AbstractModel model) {
        ModelWrapper wrapper = new ModelWrapper(model, databaseSchema.getModelMetaData(model.getClass()), augmenterHelper);
        return wrapper;
    }

    protected DmlTemplate getDmlTemplate(String templateName) {
        return this.dmlTemplates.getDmlTemplate(databaseSchema.getDeviceMode(), templateName);
    }
    @SuppressWarnings("unchecked")
    protected <T> QueryTemplate getQueryTemplate(Query<T> query) {
        QueryTemplate queryTemplate = new QueryTemplate();
        boolean isEntityResult = AbstractModel.class.isAssignableFrom(query.getResultClass());
        // defined in config
        if (queryTemplates.containsQueryTemplate(databaseSchema.getDeviceMode(), query.getName())) {
            queryTemplate = queryTemplates.getQueryTemplate(databaseSchema.getDeviceMode(), query.getName()).copy();
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
                if (current.getAnnotation(TableDef.class).ignoreSuperTableDef()) {
                    break;
                }
            }
            current = current.getSuperclass();
        }

        if (params != null) {
            verifyParameters(toProcess, params.keySet());
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
                throw new PersistException("Could not find table for the %s entity in the %s module.  Are you using the correct session?", entity.getSimpleName(), databaseSchema.getTablePrefix());
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

    /**
     * Checks the given Set of query parameter property names can be found as a mapped column within the given list
     * of entityModelClasses.
     * @param entityModelClasses The list of classes to search
     * @param queryParamKeys The list of property names to search for
     * @throws PersistException if at least one property in the list of given queryParamKeys cannot be found within
     * the given list of classes.
     */
    protected void verifyParameters(List<Class<?>> entityModelClasses, Set<String> queryParamKeys) {
        if (queryParamKeys != null && ! queryParamKeys.isEmpty() && entityModelClasses != null) {
            for (String property: queryParamKeys) {
                boolean isPropertyMapped = entityModelClasses.stream().anyMatch(modelClass -> {
                    ModelMetaData modelMetaData = this.databaseSchema.getModelMetaData(modelClass);
                    return modelMetaData.getColumnNameForProperty(modelClass, property) != null ||
                            isAugmentedProperty(modelClass, property);
                });

                if (! isPropertyMapped) {
                    throw new PersistException(
                            String.format("A @ColumnDef mapping for property '%s' could not be " +
                                            "found in any of the following classes: %s",
                                    property,
                                    entityModelClasses.stream().map(c -> c.getSimpleName()).collect(Collectors.joining(", "))
                            )
                    );
                }
            }
        }
    }

    private boolean isAugmentedProperty(Class<?> modelClass, String property) {
        boolean hasProperty = false;
        List<AugmenterConfig> configs = augmenterHelper.getAugmenterConfigs(modelClass);
        for (AugmenterConfig config : configs) {
            hasProperty = config.getAugmenter(property) != null;
            if (hasProperty) {
                break;
            }
        }
        return hasProperty;
    }

    public void save(AbstractModel argModel) {
        List<Table> tables = getValidatedTables(argModel);

        ModelWrapper model =
                new ModelWrapper(argModel, databaseSchema.getModelMetaData(argModel.getClass()), augmenterHelper);

        setMaintenanceValues(model);
        setTagValues(model);
        model.load();
        model.loadValues();

        for (Table table : tables) {
            if (model.isNew()) {
                try {
                    insert(model, table);
                } catch (DuplicateKeyException ex) {
                    if (log.isDebugEnabled()) {
                        log.info("Insert of entity failed, failing over to an update: " + argModel, ex);
                    } else {
                        log.info("Insert of entity failed, failing over to an update: " + argModel);
                    }
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

        model.setRetrievalTime(new Date());
    }

    public void delete(AbstractModel argModel) {
        List<Table> tables = getValidatedTables(argModel);

        ModelWrapper model =
                new ModelWrapper(argModel, databaseSchema.getModelMetaData(argModel.getClass()), augmenterHelper);

        model.load();
        model.loadValues();

        for (Table table : tables)  {
            delete(model, table);
        }
    }

    protected void delete(ModelWrapper model, Table table) {
        excecuteDml(DmlType.DELETE, model, table);
    }

    protected void insert(ModelWrapper model, Table table) {
        if (0 == excecuteDml(DmlType.INSERT, model, table)) {
            throw new DuplicateKeyException("Received 0 rows during insert to " + table.getName());
        }
    }

    protected int update(ModelWrapper model, Table table) {
        return excecuteDml(DmlType.UPDATE, model, table);
    }

    protected int excecuteDml(DmlType type, ModelWrapper model, Table table) {
        boolean[] nullKeyValues = model.getNullKeys();
        List<Column> primaryKeyColumns = getPrimaryKeyWithTags(model, table);

        DmlStatement statement = databasePlatform.createDmlStatement(type, table.getCatalog(), table.getSchema(), table.getName(),
                primaryKeyColumns.toArray(new Column[primaryKeyColumns.size()]), model.getColumns(table), nullKeyValues, null);
        String sql = statement.getSql();
        Object[] values = statement.getValueArray(model.getColumnNamesToValues());
        int[] types = statement.getTypes();

        try {
            return jdbcTemplate.getJdbcOperations().update(sql, values, types);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Failed to execute " + type + " statement: " + new LogSqlBuilder().buildDynamicSqlForLog(sql, values, types), e);
        } catch (Exception ex) {
            throw new PersistException("Failed to execute " + type + " statement: " + new LogSqlBuilder().buildDynamicSqlForLog(sql, values, types), ex);
        }
    }

    protected void batchInternal(List<? extends  AbstractModel> models, DmlType dmlType) {
        if (models.size() > 0) {
            AbstractModel exampleModel = models.get(0);

            ModelWrapper model = new ModelWrapper(exampleModel, databaseSchema.getModelMetaData(exampleModel.getClass()), augmenterHelper);
            setMaintenanceValues(model);
            setTagValues(model);
            model.load();
            model.loadValues();

            List<Table> tables = getValidatedTables(exampleModel);
            for (Table table : tables) {
                boolean[] nullKeyValues = model.getNullKeys();
                List<Column> primaryKeyColumns = getPrimaryKeyWithTags(model, table);

                DmlStatement statement = databasePlatform.createDmlStatement(dmlType, table.getCatalog(), table.getSchema(), table.getName(),
                        primaryKeyColumns.toArray(new Column[primaryKeyColumns.size()]), model.getColumns(table), nullKeyValues, null);
                String sql = statement.getSql();
                jdbcTemplate.getJdbcOperations().batchUpdate(sql, getValueArray(statement, models), model.getColumnTypes(table));
            }
        }
    }

    private List<Column> getPrimaryKeyWithTags(ModelWrapper model, Table table) {
        List<Column> primaryKeyColumns = new ArrayList<>(model.getPrimaryKeyColumns());
        if (isTaggedWithPrimaryKey(model.getModel())) {
            primaryKeyColumns.addAll(Arrays.stream(model.getColumns(table))
                    .filter(c -> StringUtils.startsWith(c.getName(), TagModel.TAG_PREFIX))
                    .collect(Collectors.toList()));
        }
        return primaryKeyColumns;
    }

    private boolean isTaggedWithPrimaryKey(AbstractModel model) {
        if (model != null && model instanceof ITaggedModel) {
            Tagged tagged = model.getClass().getAnnotation(Tagged.class);
            return tagged != null && tagged.includeTagsInPrimaryKey();
        }
        return false;
    }

    public void batchInsert(List<? extends AbstractModel> models) {
        batchInternal(models, DmlType.INSERT);
    }

    public void batchDelete(List<? extends AbstractModel> models) {
        batchInternal(models, DmlType.DELETE);
    }

    public void batchUpdate(List<? extends AbstractModel> models) {
        batchInternal(models, DmlType.UPDATE);
    }

    private List<Object[]> getValueArray(DmlStatement statement, List<? extends AbstractModel> models) {
        List<Object[]> values = new ArrayList<>(models.size());
        final ModelMetaData meta = databaseSchema.getModelMetaData(models.get(0).getClass());
        models.forEach(m -> {
            ModelWrapper model = new ModelWrapper(m, meta, augmenterHelper);
            setMaintenanceValues(model);
            setTagValues(model);
            model.load();
            model.loadValues();
            values.add(statement.getValueArray(model.getColumnNamesToValues()));
        });
        return values;
    }

    protected List<Table> getValidatedTables(AbstractModel entity) {
        if (entity == null) {
            throw new PersistException("Failed to locate a database table for null entity class");
        }
        return getValidatedTables(entity.getClass());
    }

    protected List<Table> getValidatedTables(Class<?> entityClass) {
        List<Table> tables = databaseSchema.getTables(databaseSchema.getDeviceMode(), entityClass);
        if (tables == null || tables.size() == 0) {
            throw new PersistException("Failed to locate a database table for entity class: '" + entityClass
                    + "'. Make sure the correct dbSession is used with the module by using the correct @Qualifier");
        }
        return tables;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> queryInternal(Class<T> resultClass, SqlStatement statement, int maxResults) throws Exception {
        List<T> objects = new ArrayList<T>();
        String sqlToUse = null;
        Object[] args = null;
        try {
            ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(statement.getSql());
            sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, statement.getParameters());
            List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, statement.getParameters());
            args = NamedParameterUtils.buildValueArray(parsedSql, statement.getParameters(), declaredParameters);
            PreparedStatementCreator psc = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters).newPreparedStatementCreator(args);
            List<Row> rows = jdbcTemplate.getJdbcOperations().execute(psc, new PreparedStatementCallback<List<Row>>() {
                @Override
                public List<Row> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    Connection con = ps.getConnection();
                    boolean autoCommitBefore = con.getAutoCommit();
                    List<Row> results = new ArrayList<>();
                    DefaultMapper mapper = new DefaultMapper();
                    try {
                        ps.setFetchSize(maxResults < 10000 ? maxResults : 10000);
                        ps.setMaxRows(maxResults);
                        // jumped through all these hoops just to set auto commit to false for postgres
                        con.setAutoCommit(false);
                        ResultSet rs = ps.executeQuery();
                        int rowCount = 0;
                        while (rs.next() && rowCount < maxResults) {
                            Row row = mapper.mapRow(rs, ++rowCount);
                            if (row != null) {
                                results.add(row);
                            }
                        }
                        rs.close();
                        return results;
                    } finally {
                        con.setAutoCommit(autoCommitBefore);
                    }
                }
            });
            for (int j = 0; j < rows.size(); j++) {
                Row row = rows.get(j);
                T object = null;

                if (resultClass != null) {
                    if (resultClass.equals(String.class)) {
                        object = (T) row.stringValue();
                    } else if (resultClass.getPackage().getName().equals("java.lang") ||
                            resultClass.getPackage().getName().equals("java.util") ||
                            resultClass.getPackage().getName().equals("java.sql") ||
                            resultClass.getPackage().getName().equals("java.math")) {
                        object = (T) row.values().iterator().next();
                        if (object != null && !resultClass.isAssignableFrom(object.getClass())) {
                            throw new PersistException(object.getClass().getName() + " is not assignable to " + resultClass.getName());
                        }
                    } else if (isModel(resultClass)) {
                        object = mapModel(resultClass, row);
                    } else {
                        object = mapNonModel(resultClass, row);
                    }
                } else {
                    object = (T) mapNonModel(row);
                }
                objects.add(object);
            }
        } catch (Exception ex) {
            try {
                LogSqlBuilder builder = new LogSqlBuilder();
                Object[] rawArgs = cleanArgs(args);
                String sql = builder.buildDynamicSqlForLog(sqlToUse, rawArgs, null);
                throw new PersistException("Failed to execute sql: " + sql, ex);
            } catch (Exception ex2) {
                if (ex2 instanceof PersistException) {
                    throw ex2;
                } else {
                    log.warn("Could not generate dynamic sql to log.", ex2);
                    throw ex; // throw the first.
                }
            }

        }

        return objects;
    }

    protected Object[] cleanArgs(Object[] args) {
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            if (value instanceof  SqlParameterValue) {
                value = ((SqlParameterValue)value).getValue();
            }
            newArgs[i] = value;
        }

        return newArgs;
    }

    protected boolean isModel(Class<?> resultClass) {
        return AbstractModel.class.isAssignableFrom(resultClass) && resultClass.getDeclaredAnnotation(TableDef.class) != null;
    }

    @SuppressWarnings("unchecked")
    protected <T> T mapModel(Class<T> resultClass, Row row) throws Exception {
        ModelMetaData modelMetaData = databaseSchema.getModelMetaData(resultClass);

        T object = resultClass.newInstance();
        ModelWrapper model = new ModelWrapper((AbstractModel) object, modelMetaData, augmenterHelper);
        model.load();

        LinkedCaseInsensitiveMap<String> matchedColumns = new LinkedCaseInsensitiveMap<>();
        LinkedHashMap<String, Object> deferredLoadValues = new LinkedHashMap<>();

        mapModelHelper(modelMetaData, row, object, model, resultClass, matchedColumns, deferredLoadValues);

        if(model.getModel().getExtensions() != null){
            model.getModel().getExtensions().forEach( (clazz, o) ->
                    mapModelHelper(modelMetaData, row, o, model, clazz,matchedColumns,deferredLoadValues));
        }

        addTags(row, matchedColumns, (AbstractModel) object);
        addAugments(row, matchedColumns, (AbstractModel) object);
        addUnmatchedColumns(row, matchedColumns, (AbstractModel) object);
        decorateRetrievedModel(model);

        return (T) model.getModel();
    }

    private void mapModelHelper(
            ModelMetaData modelMetaData,
            Row row,
            Object object,
            ModelWrapper modelWrapper,
            Class<?> resultClass,
            LinkedCaseInsensitiveMap<String> matchedColumns,
            LinkedHashMap<String, Object> deferredLoadValues){
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);

        for (int i = 0; i < propertyDescriptors.length; i++) {
            String propertyName = propertyDescriptors[i].getName();
            String columnName = modelMetaData.getColumnNameForProperty(resultClass, propertyName);
            if (columnName != null) {
                if (row.containsKey(columnName)) {
                    Object value = row.get(columnName);
                    if (isDefferedLoadField(modelWrapper.getField(propertyName))) {
                        deferredLoadValues.put(propertyName, value);
                    } else {
                        modelWrapper.setValue(propertyName, value);
                        matchedColumns.put(columnName, null);
                    }
                }
            }
        }

        for (String propertyName : deferredLoadValues.keySet()) {
            Object value = deferredLoadValues.get(propertyName);
            modelWrapper.setValue(propertyName, value);
            String columnName = DatabaseSchema.camelToSnakeCase(propertyName);
            matchedColumns.put(columnName, null);
        }

    }

    protected void addTags(Row row, LinkedCaseInsensitiveMap<String> matchedColumns, AbstractModel model) {
        if (model instanceof ITaggedModel) {
            Map<String, Object> tagValues = new HashMap<>();
            for (String columnName : row.keySet()) {
                if (columnName.toUpperCase().startsWith(TagModel.TAG_PREFIX)) {
                    matchedColumns.put(columnName, null);
                    tagValues.put(columnName, row.getString(columnName));
                }
            }
            tagHelper.addTags((ITaggedModel) model, tagValues);
        }
    }

    protected void addAugments(Row row, LinkedCaseInsensitiveMap<String> matchedColumns, AbstractModel model) {
        if (model instanceof IAugmentedModel) {
            List<AugmenterConfig> configs = augmenterHelper.getAugmenterConfigs(model);
            if (CollectionUtils.isEmpty(configs)) {
                log.info("No augmenter columns defined for the model: " + model.getClass().getSimpleName());
                return;
            }
            Map<String, Object> augmentsValues = new HashMap<>();
            for (String columnName : row.keySet()) {
                for (AugmenterConfig config : configs) {
                    if (columnName.toUpperCase().startsWith(config.getPrefix())) {
                        matchedColumns.put(columnName, null);
                        augmentsValues.put(columnName, row.getString(columnName));
                    }
                }
                augmenterHelper.addAugments((IAugmentedModel) model, augmentsValues);
            }
        }
    }

    protected boolean isDefferedLoadField(Field field) {
        return field.getType().isAssignableFrom(Money.class) || ITypeCode.class.isAssignableFrom(field.getClass());
    }

    private Map<String, Object> mapNonModel(Row row) {
        return row;
    }



    protected <T> T mapNonModel(Class<T> resultClass, Row row) throws Exception {
        T object = resultClass.newInstance();
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);

        LinkedCaseInsensitiveMap<String> matchedColumns = new LinkedCaseInsensitiveMap<>();

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
        long ts = System.currentTimeMillis();
        int count = 0;
        for (AbstractModel entity : entities) {
            save(entity);
            count++;
            if (System.currentTimeMillis() - ts > 30000) {
                ts = System.currentTimeMillis();
                log.info("Saved {} {} rows", count, entity.getClass().getSimpleName());
            }
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
