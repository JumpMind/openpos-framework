package org.jumpmind.pos.context;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.pos.context.model.ConfigModel;
import org.jumpmind.pos.context.model.ContextRepository;
import org.jumpmind.pos.context.model.Node;
import org.jumpmind.pos.context.model.TagModel;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.impl.EntityMetaData;
import org.jumpmind.pos.service.IDBSchemaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ContextDBSchemaListener")
public class ContextSchemaEnhancer implements IDBSchemaListener {
    
    private final static Logger log = LoggerFactory.getLogger(ContextSchemaEnhancer.class);
    
    private DBSessionFactory sessionFactory;

    @Override
    public void beforeSchemaCreate(DBSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        List<TagModel> tags = ContextRepository.getTagConfig().getTags();
        enchanceTaggedTable(Node.class, tags);
        enchanceTaggedTable(ConfigModel.class, tags);
    }

    @Override
    public void afterSchemaCreate(DBSessionFactory sessionFactory) {
        // No action.
    }
    
    protected void enchanceTaggedTable(Class<?> entityClass, List<TagModel> tags) {
        EntityMetaData entityMetaData = 
                sessionFactory.getDatabaseSchema().getClassMetadata().get(entityClass);
        Table table = entityMetaData.getTable();
        warnOrphanedTagColumns(tags, table);
        modifyTagColumns(tags, table);
        addTagColumns(tags, table);
        sessionFactory.getDatabaseSchema().modifyTable(table);
    }

    protected void modifyTagColumns(List<TagModel> tags, Table table) {
        for (Column existingColumn : table.getColumns()) {
            for (TagModel tag : tags) {
                if (StringUtils.equalsIgnoreCase(getColumnName(tag), existingColumn.getName())) {
                    setColumnInfo(existingColumn, tag);
                    break;
                }
            }
        }        
    }

    protected void addTagColumns(List<TagModel> tags, Table table) {
        for (TagModel tag : tags) {
            if (table.getColumnIndex(getColumnName(tag)) == -1) {
                Column tagColumn = generateTagColumn(tag);
                table.addColumn(tagColumn);
            }
        }
    }

    protected void warnOrphanedTagColumns(List<TagModel> tags, Table table) {
        for (Column existingColumn : table.getColumns()) {
            if (! existingColumn.getName().toUpperCase().startsWith(TagModel.TAG_PREFIX)) {
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
                log.info("Orphaned tag column detected.  This column should be manually dropped: " + 
                        table + " " + existingColumn);
            }
        }
    }
    
    protected Column generateTagColumn(TagModel tag) {
        return setColumnInfo(new Column(), tag);
    }
    
    protected Column setColumnInfo(Column column, TagModel tag) {
        column.setName(getColumnName(tag));
        if (StringUtils.equals(TagModel.TAG_NUMERIC_TYPE, tag.getDataType())) {
            column.setTypeCode(Types.BIGINT);
        } else {
            column.setTypeCode(Types.VARCHAR);
            column.setSize("128");
        }
        return column;        
    }
    
    protected String getColumnName(TagModel tag) {
        return TagModel.TAG_PREFIX + tag.getName().toUpperCase();
    }

}
