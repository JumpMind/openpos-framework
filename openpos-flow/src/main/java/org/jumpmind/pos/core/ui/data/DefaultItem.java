package org.jumpmind.pos.core.ui.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.FormDisplayField;
import org.jumpmind.pos.core.ui.IItem;

public class DefaultItem implements IItem, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Integer index;
    private String description;
    protected String subtitle;
    private String amount;
    private List<String> labels = new ArrayList<>();
    private List<FormDisplayField> fields = new ArrayList<>();;
    private boolean selected = false;
    private String type;
    private boolean enabled = true;

    public DefaultItem() {}
    
    public DefaultItem(String description, String subtitle, String amount) {
        this.description = description;
        this.subtitle = subtitle;
        this.amount = amount;
    }
    
    @Override
    public Integer getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }
    @Override
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    @Override
    public String getAmount() {
        return amount;
    }
    @Override
    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public List<FormDisplayField> getFields() {
        return fields;
    }

    @Override
    public void setFields(List<FormDisplayField> fields) {
        this.fields = fields;
    }
    
    public void addField(FormDisplayField field) {
        this.fields.add(field);
    }
    
    @Override
    public boolean isSelected() {
        return this.selected;
    }
    
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    } 
    
    public void addLabel( String label ) {
        this.labels.add( label );
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;        
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
}

