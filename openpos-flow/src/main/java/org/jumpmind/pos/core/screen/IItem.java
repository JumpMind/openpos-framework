package org.jumpmind.pos.core.screen;

import java.util.List;

import org.jumpmind.pos.core.model.FormDisplayField;

public interface IItem {
    public Integer getIndex();
    public void setIndex(Integer index);
    public String getID();
    public void setID(String id);
    public String getDescription();
    public void setDescription(String description);
    public String getSubtitle();
    public void setSubtitle(String subtitle);
    public List<FormDisplayField> getFields();
    public void setFields(List<FormDisplayField> fields);
    public String getAmount();
    public void setAmount(String amount);
    public boolean isSelected();
    public void setSelected(boolean selected);
    public void setEnabled(boolean enabled);
    public boolean isEnabled();
}
