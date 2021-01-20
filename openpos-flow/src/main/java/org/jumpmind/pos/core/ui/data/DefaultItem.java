package org.jumpmind.pos.core.ui.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jumpmind.pos.core.model.FormDisplayField;
import org.jumpmind.pos.core.ui.IItem;

@Data
public class DefaultItem implements IItem, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String iD;
    private Integer index;
    private String description;
    protected String subtitle;
    private String amount;
    private List<String> labels = new ArrayList<>();
    private List<FormDisplayField> fields = new ArrayList<>();;
    private boolean selected = false;
    private String type;
    private String lineItemType;
    private boolean enabled = true;

    public DefaultItem() {}
    
    public DefaultItem(String description, String subtitle, String amount) {
        this.description = description;
        this.subtitle = subtitle;
        this.amount = amount;
    }
    
    public void addField(FormDisplayField field) {
        this.fields.add(field);
    }
    
    public void addLabel( String label ) {
        this.labels.add( label );
    }
}

