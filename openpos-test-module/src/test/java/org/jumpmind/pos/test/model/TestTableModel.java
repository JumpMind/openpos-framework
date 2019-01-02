package org.jumpmind.pos.test.model;

import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name="table")
public class TestTableModel extends AbstractModel {

    private static final long serialVersionUID = 1L;

    @ColumnDef(primaryKey = true)
    int id;

    @ColumnDef
    String example;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setExample(String data) {
        this.example = data;
    }

    public String getExample() {
        return example;
    }

}
