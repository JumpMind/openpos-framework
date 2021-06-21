package org.jumpmind.pos.persist;

import java.io.InputStream;
import java.net.URL;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.pos.persist.impl.*;
import org.jumpmind.pos.persist.model.*;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.properties.TypedProperties;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
public class DBSessionFactory {

    DatabaseSchema databaseSchema;
    QueryTemplates queryTemplates;
    DmlTemplates dmlTemplates;
    IDatabasePlatform databasePlatform;
    TypedProperties sessionContext;
    @Getter
    List<Class<?>> modelClasses;
    @Getter
    List<Class<?>> modelExtensionClasses;
    TagHelper tagHelper;
    AugmenterHelper augmenterHelper;
    ClientContext clientContext;
    ShadowTablesConfigModel shadowTablesConfig;

    private static final String DEFAULT_COLUMN_SIZE = "32";

    public void init(
            IDatabasePlatform databasePlatform,
            TypedProperties sessionContext,
            List<Class<?>> entities,
            List<Class<?>> extensionEntities,
            TagHelper tagHelper,
            AugmenterHelper augmenterHelper,
            ClientContext clientContext,
            ShadowTablesConfigModel shadowTablesConfig) {

        QueryTemplates queryTemplates = getQueryTemplates(sessionContext.get("module.tablePrefix"));
        DmlTemplates dmlTemplates = getDmlTemplates(sessionContext.get("module.tablePrefix"));

        init(databasePlatform, sessionContext, entities, extensionEntities, queryTemplates, dmlTemplates, tagHelper, augmenterHelper, clientContext, shadowTablesConfig);
    }

    public void init(
            IDatabasePlatform databasePlatform,
            TypedProperties sessionContext,
            List<Class<?>> entities,
            List<Class<?>> extensionEntities,
            QueryTemplates queryTemplatesObject,
            DmlTemplates dmlTemplates,
            TagHelper tagHelper,
            AugmenterHelper augmenterHelper,
            ClientContext clientContext,
            ShadowTablesConfigModel shadowTablesConfig) {

        this.queryTemplates = queryTemplatesObject;
        this.dmlTemplates = dmlTemplates;
        this.sessionContext = sessionContext;

        this.databasePlatform = databasePlatform;
        this.modelClasses = entities;
        this.modelExtensionClasses = extensionEntities;
        this.tagHelper = tagHelper;
        this.augmenterHelper = augmenterHelper;

        this.clientContext = clientContext;
        this.shadowTablesConfig = shadowTablesConfig;

        this.initSchema();
    }

    protected void initSchema() {
        this.databaseSchema = new DatabaseSchema();
        databaseSchema.init(sessionContext.get("module.tablePrefix"), databasePlatform,
                this.modelClasses.stream().filter(e -> e.getAnnotation(org.jumpmind.pos.persist.TableDef.class) != null)
                        .collect(Collectors.toList()),
                this.modelExtensionClasses,
                this.augmenterHelper,
                this.clientContext,
                this.shadowTablesConfig
        );

        this.queryTemplates.replaceModelClassNamesWithTableNames(this.databaseSchema, this.modelClasses, (shadowTablesConfig != null) && shadowTablesConfig.validateTablesInQueries());
        this.dmlTemplates.replaceModelClassNamesWithTableNames(this.databaseSchema, this.modelClasses, (shadowTablesConfig != null) && shadowTablesConfig.validateTablesInQueries());
    }

    public void createAndUpgrade() {
        enhanceTaggedModels();
        augmentModels();
        databaseSchema.createAndUpgrade();
    }

    public List<Table> getTables(Class<?>... exclude) {
        List<Table> list = new ArrayList<>();
        List<Class<?>> toExclude = exclude != null ? Arrays.asList(exclude) : Collections.emptyList();
        for (Class<?> modelClazz : this.modelClasses) {
            if (!toExclude.contains(modelClazz)) {
                //  Note that the list below returns regular tables only, NOT shadow tables.
                List<Table> tables = this.databaseSchema.getTables("default", modelClazz);
                list.addAll(tables);
            }
        }
        return list;
    }

    public DBSession createDbSession() {
        return new DBSession(null, null, databaseSchema, databasePlatform, sessionContext, queryTemplates, dmlTemplates, tagHelper, augmenterHelper);
    }

    public org.jumpmind.db.model.Table getTableForEnhancement(String deviceMode, Class<?> entityClazz) {
        List<org.jumpmind.db.model.Table> tables = this.databaseSchema.getTables(deviceMode, entityClazz);
        return tables != null && tables.size() > 0 ? tables.get(0) : null;
    }

    public static QueryTemplates getQueryTemplates(String tablePrefix) {
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(tablePrefix + "-query.yml");

            QueryTemplates templates = new QueryTemplates();
            
            if(!urls.hasMoreElements()) {
                log.debug("Could not locate " + tablePrefix + "-query.yml on the classpath.");
                return new QueryTemplates();
            }
            
            while(urls.hasMoreElements()){
                URL url = urls.nextElement();
                log.info(String.format("Loading %s...", url.toString()));
                InputStream queryYamlStream = url.openStream();
                QueryTemplates queryTemplates = new Yaml(new Constructor(QueryTemplates.class)).load(queryYamlStream);
                if (queryTemplates != null) {
                    templates.addQueries("default", queryTemplates.getQueries());
                    templates.addQueries("training", queryTemplates.getQueries());
                }
            }
            
            return templates;

        } catch (Exception ex) {
            log.error("ERROR: Failed to load query file " + tablePrefix + "-query.yml");
            throw new PersistException("Failed to load " + tablePrefix + "-query.yml", ex);
        }
    }

    public static DmlTemplates getDmlTemplates(String tablePrefix) {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(tablePrefix + "-dml.yml");
            if (url != null) {
                log.info(String.format("Loading %s...", url.toString()));
                InputStream queryYamlStream = url.openStream();
                DmlTemplates dmlTemplates = new Yaml(new Constructor(DmlTemplates.class)).load(queryYamlStream);
                if (dmlTemplates != null) {
                    dmlTemplates.addDmls("default", dmlTemplates.getDmls());
                    dmlTemplates.addDmls("training", dmlTemplates.getDmls());
                }

                return dmlTemplates;
            } else {
                log.debug("Could not locate " + tablePrefix + "-dml.yml on the classpath.");
                return new DmlTemplates();
            }
        } catch (Exception ex) {
            log.error("ERROR: Failed to load DML query file " + tablePrefix + "-dml.yml");
            throw new PersistException("Failed to load " + tablePrefix + "-dml.yml", ex);
        }
    }

    protected void augmentModels() {
        if (augmenterHelper != null) {
            AugmenterConfigs augmenterConfigs = augmenterHelper.getAugmenterConfigs();
            
            for (Class<?> clazz : modelClasses) {
                Augmented[] annotations = clazz.getAnnotationsByType(Augmented.class);
                if (annotations.length > 0) {
                    AugmenterConfig augmenterConfig = augmenterConfigs.getConfigByName(annotations[0].name());
                    if (augmenterConfig != null) {
                        augmentTable(clazz, augmenterConfig);
                    }
                    else {
                        log.info("Missing augmenter name " + annotations[0].name() + " defined in augmenter configuration");
                    }
                }
            }
        }
    }

    protected void augmentTable(Class<?> entityClass, AugmenterConfig augmenterConfig) {
        //  Normal table.

        Table table = getTableForEnhancement("default", entityClass);
        warnOrphanedAugmentedColumns(augmenterConfig, table);
        modifyAugmentColumns(augmenterConfig, table);
        addAugmentColumns(augmenterConfig, table);

        //  The corresponding shadow table, if any.

        Table shadowTable = getTableForEnhancement("training", entityClass);
        if ((shadowTable != null) && !shadowTable.getName().equalsIgnoreCase(table.getName())) {
            warnOrphanedAugmentedColumns(augmenterConfig, shadowTable);
            modifyAugmentColumns(augmenterConfig, shadowTable);
            addAugmentColumns(augmenterConfig, shadowTable);
        }
    }

    protected void addAugmentColumns(AugmenterConfig augmenterConfig, Table table) {
        for (AugmenterModel augmenter : augmenterConfig.getAugmenters()) {
            if (table.getColumnIndex(getColumnName(augmenterConfig.getPrefix(), augmenter)) == -1) {
                Column tagColumn = generateAugmentColumn(augmenterConfig, augmenter);
                table.addColumn(tagColumn);
            }
        }
    }

    protected Column generateAugmentColumn(AugmenterConfig augmenterConfig, AugmenterModel augmenter) {
        return setColumnInfo(new Column(), augmenterConfig.getPrefix(), augmenter);
    }

    protected void modifyAugmentColumns(AugmenterConfig augmenterConfig, Table table) {
        for (Column existingColumn : table.getColumns()) {
            for (AugmenterModel augmenter : augmenterConfig.getAugmenters()) {
                if (StringUtils.equalsIgnoreCase(getColumnName(augmenterConfig.getPrefix(), augmenter), existingColumn.getName())) {
                    setColumnInfo(existingColumn, augmenterConfig.getPrefix(), augmenter);
                    break;
                }
            }
        }
    }

    protected Column setColumnInfo(Column column, String prefix, AugmenterModel augmenter) {
        column.setName(getColumnName(prefix, augmenter));
        column.setDefaultValue(augmenter.getDefaultValue());
        column.setTypeCode(Types.VARCHAR);
        if (augmenter.getSize() != null && augmenter.getSize() > 0) {
            column.setSize(String.valueOf(augmenter.getSize()));
        } else {
            column.setSize(DEFAULT_COLUMN_SIZE);
        }
        return column;
    }

    private void warnOrphanedAugmentedColumns(AugmenterConfig augmenterConfig, Table table) {
        for (Column existingColumn : table.getColumns()) {
            if (!existingColumn.getName().toUpperCase().startsWith(augmenterConfig.getPrefix())) {
                continue;
            }

            boolean matched = false;
            for (AugmenterModel augmenter : augmenterConfig.getAugmenters()) {
                if (StringUtils.equalsIgnoreCase(getColumnName(augmenterConfig.getPrefix(), augmenter), existingColumn.getName())) {
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

    protected void enhanceTaggedModels() {
        if (tagHelper != null) {
            List<TagModel> tags = tagHelper.getTagConfig().getTags();

            for (Class<?> clazz : modelClasses) {
                Tagged[] annotations = clazz.getAnnotationsByType(Tagged.class);
                if (annotations.length > 0 || ITaggedModel.class.isAssignableFrom(clazz)) {
                    boolean includeTagsInPrimaryKey = true;
                    if (annotations.length > 0) {
                        includeTagsInPrimaryKey = annotations[0].includeTagsInPrimaryKey();
                    }
                    enhanceTaggedTable(clazz, tags, includeTagsInPrimaryKey);
                }
            }
        }
    }

    protected void enhanceTaggedTable(Class<?> entityClass, List<TagModel> tags, boolean includeInPk) {
        //  Normal table.

        Table table = getTableForEnhancement("default", entityClass);
        warnOrphanedTagColumns(tags, table);
        modifyTagColumns(tags, table, includeInPk);
        addTagColumns(tags, table, includeInPk);

        //  The corresponding shadow table, if any.

        Table shadowTable = getTableForEnhancement("training", entityClass);
        if ((shadowTable != null) && !shadowTable.getName().equalsIgnoreCase(table.getName()))  {
            warnOrphanedTagColumns(tags, shadowTable);
            modifyTagColumns(tags, shadowTable, includeInPk);
            addTagColumns(tags, shadowTable, includeInPk);
        }
    }

    protected void modifyTagColumns(List<TagModel> tags, Table table, boolean includeInPk) {
        for (Column existingColumn : table.getColumns()) {
            for (TagModel tag : tags) {
                if (StringUtils.equalsIgnoreCase(getColumnName(tag), existingColumn.getName())) {
                    setColumnInfo(existingColumn, table, tag, includeInPk);
                    break;
                }
            }
        }
    }

    protected void addTagColumns(List<TagModel> tags, Table table, boolean modifyPk) {
        for (TagModel tag : tags) {
            if (table.getColumnIndex(getColumnName(tag)) == -1) {
                Column tagColumn = generateTagColumn(tag, table, modifyPk);
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

    protected Column generateTagColumn(TagModel tag, Table table, boolean modifyPk) {
        return setColumnInfo(new Column(), table, tag, modifyPk);
    }

    protected Column setColumnInfo(Column column, Table table, TagModel tag, boolean includeInPk) {
        column.setName(getColumnName(tag));
        column.setPrimaryKey(includeInPk);
        if (includeInPk) {
            column.setPrimaryKeySequence(table.getPrimaryKeyColumnCount() + 1);
        }
        column.setRequired(true);
        column.setDefaultValue(TagModel.TAG_ALL);
        column.setTypeCode(Types.VARCHAR);
        if (tag.getSize() > 0) {
            column.setSize(String.valueOf(tag.getSize()));
        } else {
            column.setSize(DEFAULT_COLUMN_SIZE);
        }
        return column;
    }

    protected String getColumnName(TagModel tag) {
        return databasePlatform.alterCaseToMatchDatabaseDefaultCase(TagModel.TAG_PREFIX + tag.getName().toUpperCase());
    }

    protected String getColumnName(String prefix, AugmenterModel augmenter) {
        return databasePlatform.alterCaseToMatchDatabaseDefaultCase(prefix + augmenter.getName().toUpperCase());
    }

}
