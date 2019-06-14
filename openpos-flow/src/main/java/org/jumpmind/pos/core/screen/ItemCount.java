package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.model.ComboField;
import org.jumpmind.pos.core.model.FormDisplayField;
import org.jumpmind.pos.core.model.FormField;

public class ItemCount implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer index;
    private String id;
    private String description;
    private String imageUrl;
    private String productDescription;
    private List<String> labels = new ArrayList<>();
    private List<FormDisplayField> fields = new ArrayList<>();
    private FormField field = new FormField();
    private ComboField reasonCode = new ComboField();
    private Map<String, String> reasonCodeMap = new HashMap<>();
    private List<ActionItem> menuItems = new ArrayList<>();

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<FormDisplayField> getFields() {
        return fields;
    }

    public void setFields(List<FormDisplayField> fields) {
        this.fields = fields;
    }

    public FormField getField() {
        return field;
    }

    public void setField(FormField field) {
        this.field = field;
    }

    public ComboField getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ComboField reasonCode) {
        this.reasonCode = reasonCode;
    }

    public List<ActionItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<ActionItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addField(FormDisplayField field) {
        this.fields.add(field);
    }

    public Map<String, String> getReasonCodeMap() {
        reasonCodeMap.put("M01", "Outdated");
        reasonCodeMap.put("M02", "Damaged in Store");
        reasonCodeMap.put("M03", "Damaged Shipped Merchandise");
        reasonCodeMap.put("M04", "End of Season/Deal");
        reasonCodeMap.put("M05", "Defective");
        reasonCodeMap.put("M06", "Legal Mandate");
        reasonCodeMap.put("M07", "Store Use Expense");
        reasonCodeMap.put("M08", "eCom Damaged/UnSellable");
        return reasonCodeMap;
    }

    public void setReasonCodeMap(Map<String, String> reasonCodeMap) {
        this.reasonCodeMap = reasonCodeMap;
    }

}
