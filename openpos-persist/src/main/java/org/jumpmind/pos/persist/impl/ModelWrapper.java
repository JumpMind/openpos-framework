package org.jumpmind.pos.persist.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.model.ITaggedModel;

public class ModelWrapper {
    private static Logger log = Logger.getLogger(ModelWrapper.class);
    private static final boolean FORCE_ACCESS = true;
    
    public static final String ENTITY_RETRIEVAL_TIME = "entity.retrieval.time";
    
    private List<ModelClassMetaData> modelMetaData;
    private AbstractModel model;
    
    private Map<String, Object> systemData;
    
    private LinkedHashMap<String, Column> fieldsToColumns;
    private LinkedHashMap<String, Object> columnNamesToValues;
    private List<Column> primaryKeyColumns;
    
    @SuppressWarnings("unchecked")
    public ModelWrapper(AbstractModel model, List<ModelClassMetaData> modelMetaData) {
        this.model = model;
        this.modelMetaData = modelMetaData;
        
        Field field = FieldUtils.getField(model.getClass(), "systemInfo", FORCE_ACCESS);
        try {
            if (field != null) {
                systemData = (Map<String, Object>) field.get(model);
            } else {
                systemData = new HashMap<>();
            }
        } catch (Exception ex) {
            log.debug("Failed to access hidden system field systemInfo on object: " + model, ex);
            systemData = new HashMap<>();
        } 
    }
    
    public void load() {
        fieldsToColumns = mapFieldsToColumns(model.getClass());
        primaryKeyColumns = getPrimaryKeyColumns();
        columnNamesToValues = mapColumnNamesToValues();        
    }
    
    public void put(String key, Object value) {
        systemData.put(key, value);
    }
    
    public Object get(String key) {
        return systemData.get(key);
    }
    
    public Set<String> keySet() {
        return systemData.keySet();
    }
    public boolean isNew() {
        return ! systemData.containsKey(ENTITY_RETRIEVAL_TIME);
    }
    public void setRetrievalTime(Date retrievalTime) {
        systemData.put(ENTITY_RETRIEVAL_TIME, retrievalTime);
    }
    public Date getRetrievalTime() {
        return (Date) systemData.get(ENTITY_RETRIEVAL_TIME);
    }
    
    public Date getCreateTime() {
        return model.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        model.setCreateTime(createTime);
    }

    public String getCreateBy() {
        return model.getCreateBy();
    }

    public void setCreateBy(String createBy) {
        model.setCreateBy(createBy);
    }

    public Date getLastUpdateTime() {
        return model.getLastUpdateTime();
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        model.setLastUpdateTime(lastUpdateTime);
    }

    public String getLastUpdateBy() {
        return model.getLastUpdateBy();
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        model.setLastUpdateBy(lastUpdateBy);
    }
    
    public void setAdditionalField(String fieldName, Object fieldValue) {
        model.setAdditionalField(fieldName, fieldValue);
    }    
    
    public Object getAdditionalField(String fieldName) {
        return model.getAdditionalField(fieldName);
    }
    
    public Map<String, Object> getAdditionalFields() {
        return model.getAdditionalFields();
    }    
    
    protected LinkedHashMap<String, Column> mapFieldsToColumns(Class<?> resultClass) {
        LinkedHashMap<String, Column> fieldsToColumns = new LinkedHashMap<String, Column>();
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(resultClass);
        
        for (ModelClassMetaData classMetaData : modelMetaData) {   
            Table table = classMetaData.getTable();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                String propName = propertyDescriptors[i].getName();
                Column column = table.getColumnWithName(DatabaseSchema.camelToSnakeCase(propName));
                if (column != null) {
                    fieldsToColumns.put(propName, column);
                    
                }
            }
            
            if (ITaggedModel.class.isAssignableFrom(resultClass)) {
                Column[] columns = table.getColumns();
                for (Column column : columns) {
                    if (column.getName().toLowerCase().startsWith("tag_")) {
                        fieldsToColumns.put(column.getName(), column);
                    }
                }
            }
        }
        
        fieldsToColumns = orderColumns(fieldsToColumns);
        
        return fieldsToColumns;
    }
    
    protected LinkedHashMap<String, Column> orderColumns(LinkedHashMap<String, Column> argFieldsToColumns) {
        LinkedHashMap<String, Column> orderedFieldsToColumns = new LinkedHashMap<>();
        
        // Primary keys first.
        for (Entry<String, Column> entry : argFieldsToColumns.entrySet()) {
            if (entry.getValue().isPrimaryKey()) {
                orderedFieldsToColumns.put(entry.getKey(), entry.getValue());  
            }
        }
        
        // Next regular fields
        for (Entry<String, Column> entry : argFieldsToColumns.entrySet()) {
            if (!isRowMaintenanceColumn(entry.getValue())) {
                orderedFieldsToColumns.put(entry.getKey(), entry.getValue());  
            }            
        }
        
        // Now maintenance fields.
        for (Entry<String, Column> entry : argFieldsToColumns.entrySet()) {
            if (isRowMaintenanceColumn(entry.getValue())) {
                orderedFieldsToColumns.put(entry.getKey(), entry.getValue());  
            }            
        }
        
        return orderedFieldsToColumns;
    }

    protected LinkedHashMap<String, Object> mapColumnNamesToValues() {
        try {
            LinkedHashMap<String, Object> columnNamesToObjectValues = new LinkedHashMap<String, Object>();
            Set<String> fieldNames = fieldsToColumns.keySet();
            for (String fieldName : fieldNames) {

                if (fieldName.toLowerCase().startsWith("tag_")) {
                    columnNamesToObjectValues.put(fieldName, ((ITaggedModel) model).getTagValue(fieldName.substring("tag_".length())));
                } else {
                    Column column = fieldsToColumns.get(fieldName);
                    Object value = PropertyUtils.getProperty(model, fieldName);
                    if (value instanceof Money) {
                        handleMoneyField(columnNamesToObjectValues, fieldName, column, (Money)value);    
                    } else {                        
                        columnNamesToObjectValues.put(column.getName(), value);
                    }
                }
            }
            return columnNamesToObjectValues;
        } catch (Exception ex) {
            throw new PersistException(
                    "Failed to getObjectValuesByColumnName on model " + model + " fieldsToColumns: " + fieldsToColumns, ex);
        }
    }    
    
    public void setValue(String fieldName, Object value) {
        Field field = getField(fieldName);
        
        try {            
            if (field.getType().isAssignableFrom(Money.class)
                    && value != null) {
                Field xRefField = getXRefForField(fieldName);
                String modelCurrencyCode = (String)xRefField.get(model);
                if (StringUtils.isEmpty(modelCurrencyCode)) {
                    throw new PersistException("Money field " + fieldName + " cannot be loaded because crossReference= " + getXrefName(fieldName)
                    + " does not have a value. Model: " + model); 
                }
                CurrencyUnit currency = CurrencyUnit.of(modelCurrencyCode);
                BigDecimal decimalValue = (BigDecimal)value;
                decimalValue = decimalValue.setScale(currency.getDecimalPlaces());
                value = Money.of(currency, decimalValue);
            }             
            ReflectUtils.setProperty(model, fieldName, value);
        } catch (Exception ex) {
            if (ex instanceof PersistException) {
                throw (PersistException)ex;
            } else {                
                throw new PersistException("Failed to " + fieldName + " to " + value + " on " + model, ex);
            }
        }
    }
    
    protected BigDecimal handleMoneyField(LinkedHashMap<String, Object> columnNamesToObjectValues, 
            String fieldName, Column moneyDecimalColumn, Money value) {
        if (value != null) {
            String isoCurrencyCode = value.getCurrencyUnit().getCode();
            BigDecimal decimal = value.getAmount();
            
            Field xRefField = getXRefForField(fieldName);
            
            try {
                ColumnDef xRefColumnDef = xRefField.getDeclaredAnnotation(ColumnDef.class);
                String modelCurrencyCode = (String)xRefField.get(model);
                if (StringUtils.isEmpty(modelCurrencyCode)) {
                    xRefField.set(model, isoCurrencyCode);
                } else if (!StringUtils.equals(isoCurrencyCode, modelCurrencyCode)) {
                    throw new PersistException("Money field " + fieldName + " has a crossReference= " + getXrefName(fieldName)
                            + " currency field, but the currency code does not match. Currency code on Money: " 
                            + isoCurrencyCode + " currency code on model " + modelCurrencyCode + ". Model: " + model);                
                }
                
                columnNamesToObjectValues.put(moneyDecimalColumn.getName(), decimal);
                columnNamesToObjectValues.put(xRefColumnDef.name() != null ? xRefColumnDef.name() : 
                    DatabaseSchema.camelToSnakeCase(xRefField.getName()), isoCurrencyCode);
            } catch (Exception ex) {
                throw new PersistException("Failed to set money field " + fieldName  
                        + " on model " + model);                
            }
            
            return decimal;
        } else {
            return null;
        }
    }
    
    protected String getXrefName(String fieldName) {

        Field field = getField(fieldName);
        ColumnDef mainColumnDef = field.getDeclaredAnnotation(ColumnDef.class);
        String crossReference = mainColumnDef.crossReference();
        return crossReference;
    }
    
    protected Field getXRefForField(String fieldName) {
        String crossReference = getXrefName(fieldName);
        if (StringUtils.isEmpty(crossReference)) {
            throw new PersistException("Field " + fieldName + " must define a crossReference to a field, "
                    + " such as: @ColumnDef(crossReference=\"isoCurrencyCode\"). model: " + model);
        }
        Field xRefField = getField(crossReference);
        if (xRefField == null) {
            throw new PersistException("Field " + fieldName + " has a crossReference= " + crossReference
                    + " but we could not find a field with that name on this model. Model: " + model);
        }
        xRefField.setAccessible(true);
        return xRefField;
    }
    
    public Field getField(String fieldName) {
        Field field = null;
        for (ModelClassMetaData classMetaData : modelMetaData) {
            field = classMetaData.getField(fieldName);
            if (field != null) {
                break;
            }
        }
        
        if (field != null) {
            return field;
        }  else {
            throw new PersistException("Could not find field named " + fieldName + " on model " + this);
        }
    }

    public List<Column> getPrimaryKeyColumns() {
        List<Column> keys = new ArrayList<Column>(1);
        for (Column column : fieldsToColumns.values()) {
            if (column.isPrimaryKey()) {
                keys.add(column);
            }
        }
        return keys;
    }
    
    public boolean[] getNullKeys() {
        boolean[] nullKeyValues = new boolean[primaryKeyColumns.size()];
        int i = 0;
        for (Column column : primaryKeyColumns) {
            nullKeyValues[i++] = columnNamesToValues.get(column.getName()) == null;
        }        
        return nullKeyValues;
    }
    
    protected boolean isRowMaintenanceColumn(Column column) {
        String name = column.getName().toUpperCase();
        return name.equals("CREATE_BY") 
                || name.equals("CREATE_TIME")
                || name.equals("UPDATE_BY")
                || name.equals("UPDATE_TIME");
    }

//    public Table getTable() {
//        return table;
//    }

//    public void setTable(Table table) {
//        this.table = table;
//    }
    
    public List<ModelClassMetaData> getModelMetaData() {
        return modelMetaData;
    }

    public AbstractModel getModel() {
        return model;
    }

    public void setModel(AbstractModel model) {
        this.model = model;
    }
    
    public LinkedHashMap<String, Object> getColumnNamesToValues() {
        return columnNamesToValues;
    }

    public Column[] getColumns(Table table) {
        List<Column> columns = new ArrayList<>();
        
        for (Column modelColumn : fieldsToColumns.values()) {
            for (Column tableColumn : table.getColumns()) {                
                if (modelColumn.equals(tableColumn)) {
                    columns.add(tableColumn);
                    break;
                }
            }
        }
        
        return columns.toArray(new Column[columns.size()]);
    }
}
