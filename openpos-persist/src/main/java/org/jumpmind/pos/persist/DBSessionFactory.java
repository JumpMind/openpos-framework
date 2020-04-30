package org.jumpmind.pos.persist;

import java.io.InputStream;
import java.net.URL;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.pos.persist.impl.DatabaseSchema;
import org.jumpmind.pos.persist.impl.DmlTemplate;
import org.jumpmind.pos.persist.impl.DmlTemplates;
import org.jumpmind.pos.persist.impl.QueryTemplate;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.jumpmind.pos.persist.model.ITaggedModel;
import org.jumpmind.pos.persist.model.TagHelper;
import org.jumpmind.pos.persist.model.TagModel;
import org.jumpmind.properties.TypedProperties;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DBSessionFactory {

    static Logger log = Logger.getLogger(DBSessionFactory.class);

    DatabaseSchema databaseSchema;
    Map<String, QueryTemplate> queryTemplates;
    Map<String, DmlTemplate> dmlTemplates;
    IDatabasePlatform databasePlatform;
    TypedProperties sessionContext;
    List<Class<?>> modelClazzes;
    List<Class<?>> modelExtensionClazzes;
    TagHelper tagHelper;

    public void init(IDatabasePlatform databasePlatform, TypedProperties sessionContext, List<Class<?>> entities, List<Class<?>> extensionEntities, TagHelper tagHelper) {

        QueryTemplates queryTemplates = getQueryTemplates(sessionContext.get("module.tablePrefix"));
        DmlTemplates dmlTemplates = getDmlTemplates(sessionContext.get("module.tablePrefix"));

        init(databasePlatform, sessionContext, entities, extensionEntities, queryTemplates, dmlTemplates, tagHelper);
    }

    public void init(
            IDatabasePlatform databasePlatform,
            TypedProperties sessionContext,
            List<Class<?>> entities,
            List<Class<?>> extensionEntities,
            QueryTemplates queryTemplatesObject,
            DmlTemplates dmlTemplates,
            TagHelper tagHelper) {

        this.queryTemplates = buildQueryTemplatesMap(queryTemplatesObject);
        this.dmlTemplates = buildDmlTemplatesMap(dmlTemplates);
        this.sessionContext = sessionContext;

        this.databasePlatform = databasePlatform;
        this.modelClazzes = entities;
        this.modelExtensionClazzes = extensionEntities;
        this.tagHelper = tagHelper;

        this.initSchema();
    }

    protected void initSchema() {
        this.databaseSchema = new DatabaseSchema();
        databaseSchema.init(sessionContext.get("module.tablePrefix"), databasePlatform,
                this.modelClazzes,
                this.modelExtensionClazzes);
    }

    public void createAndUpgrade() {
        enhanceTaggedModels();
        databaseSchema.createAndUpgrade();
    }

    public List<Table> getTables(Class<?>... exclude) {
        List<Table> list = new ArrayList<>();
        List<Class<?>> toExclude = exclude != null ? Arrays.asList(exclude) : Collections.emptyList();
        for (Class<?> modelClazz : this.modelClazzes) {
            if (!toExclude.contains(modelClazz)) {
                List<Table> tables = this.databaseSchema.getTables(modelClazz);
                list.addAll(tables);
            }
        }
        return list;
    }

    public DBSession createDbSession() {
        return new DBSession(null, null, databaseSchema, databasePlatform, sessionContext, queryTemplates, dmlTemplates, tagHelper);
    }

    public org.jumpmind.db.model.Table getTableForEnhancement(Class<?> entityClazz) {
        List<org.jumpmind.db.model.Table> tables = this.databaseSchema.getTables(entityClazz);
        return tables != null && tables.size() > 0 ? tables.get(0) : null;
    }

    public static QueryTemplates getQueryTemplates(String tablePrefix) {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(tablePrefix + "-query.yml");
            if (url != null) {
                log.info(String.format("Loading %s...", url.toString()));
                InputStream queryYamlStream = url.openStream();
                QueryTemplates queryTemplates = new Yaml(new Constructor(QueryTemplates.class)).load(queryYamlStream);
                return queryTemplates;
            } else {
                log.debug("Could not locate " + tablePrefix + "-query.yml on the classpath.");
                return new QueryTemplates();
            }
        } catch (Exception ex) {
            throw new PersistException("Failed to load " + tablePrefix + "-query.yml", ex);
        }
    }

    public static DmlTemplates getDmlTemplates(String tablePrefix) {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(tablePrefix + "-dml.yml");
            if (url != null) {
                log.info(String.format("Loading %s...", url.toString()));
                InputStream queryYamlStream = url.openStream();
                DmlTemplates queryTemplates = new Yaml(new Constructor(DmlTemplates.class)).load(queryYamlStream);
                return queryTemplates;
            } else {
                log.debug("Could not locate " + tablePrefix + "-query.yml on the classpath.");
                return new DmlTemplates();
            }
        } catch (Exception ex) {
            throw new PersistException("Failed to load " + tablePrefix + "-query.yml", ex);
        }
    }

    protected Map<String, QueryTemplate> buildQueryTemplatesMap(QueryTemplates queryTemplates) {
        Map<String, QueryTemplate> queryTemplatesMap = new HashMap<>();
        if (queryTemplates != null) {
            queryTemplates.getQueries().stream().forEach((q) -> queryTemplatesMap.put(q.getName(), q));
        }
        return queryTemplatesMap;
    }

    protected Map<String, DmlTemplate> buildDmlTemplatesMap(DmlTemplates dmlTemplates) {
        Map<String, DmlTemplate> dmlTemplatesMap = new HashMap<>();
        if (dmlTemplates != null && dmlTemplates.getDmls() != null) {
            dmlTemplates.getDmls().stream().forEach((q) -> dmlTemplatesMap.put(q.getName(), q));
        }
        return dmlTemplatesMap;
    }

    protected void enhanceTaggedModels() {
        if (tagHelper != null) {
            List<TagModel> tags = tagHelper.getTagConfig().getTags();

            for (Class<?> clazz : modelClazzes) {
                Tagged[] annotations = clazz.getAnnotationsByType(Tagged.class);
                if (annotations.length > 0 || ITaggedModel.class.isAssignableFrom(clazz)) {
                    boolean includeTagsInPrimaryKey = true;
                    if (annotations.length > 0) {
                        includeTagsInPrimaryKey = annotations[0].includeTagsInPrimaryKey();
                    }
                    enchanceTaggedTable(clazz, tags, includeTagsInPrimaryKey);
                }
            }
        }
    }

    protected void enchanceTaggedTable(Class<?> entityClass, List<TagModel> tags, boolean includeInPk) {
        Table table = getTableForEnhancement(entityClass);
        warnOrphanedTagColumns(tags, table);
        modifyTagColumns(tags, table, includeInPk);
        addTagColumns(tags, table, includeInPk);
    }

    protected void modifyTagColumns(List<TagModel> tags, Table table, boolean includeInPk) {
        for (Column existingColumn : table.getColumns()) {
            for (TagModel tag : tags) {
                if (StringUtils.equalsIgnoreCase(getColumnName(tag), existingColumn.getName())) {
                    setColumnInfo(existingColumn, tag, includeInPk);
                    break;
                }
            }
        }
    }

    protected void addTagColumns(List<TagModel> tags, Table table, boolean modifyPk) {
        for (TagModel tag : tags) {
            if (table.getColumnIndex(getColumnName(tag)) == -1) {
                Column tagColumn = generateTagColumn(tag, modifyPk);
                table.addColumn(tagColumn);
            }
        }
    }

    protected void warnOrphanedTagColumns(List<TagModel> tags, Table table) {
        for (Column existingColumn : table.getColumns()) {
            if (!existingColumn.getName().toUpperCase().startsWith(TagModel.TAG_PREFIX)) {
                continue;
            }

            boolean matched = false;
            for (TagModel tag : tags) {
                if (StringUtils.equalsIgnoreCase(getColumnName(tag), existingColumn.getName())) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                log.info("Orphaned tag column detected.  This column should be manually dropped if no longer needed: " + table + " "
                        + existingColumn);
            }
        }
    }

    protected Column generateTagColumn(TagModel tag, boolean modifyPk) {
        return setColumnInfo(new Column(), tag, modifyPk);
    }

    protected Column setColumnInfo(Column column, TagModel tag, boolean includeInPk) {
        column.setName(getColumnName(tag));
        column.setPrimaryKey(includeInPk);
        column.setRequired(true);
        column.setDefaultValue(TagModel.TAG_ALL);
        column.setTypeCode(Types.VARCHAR);
        if (tag.getSize() > 0) {
            column.setSize(String.valueOf(tag.getSize()));
        } else {
            column.setSize("32");
        }
        return column;
    }

    protected String getColumnName(TagModel tag) {
        return databasePlatform.alterCaseToMatchDatabaseDefaultCase(TagModel.TAG_PREFIX + tag.getName().toUpperCase());
    }

}
