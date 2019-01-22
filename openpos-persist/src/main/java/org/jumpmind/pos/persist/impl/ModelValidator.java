package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.joda.money.Money;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.PersistException;

public class ModelValidator {

    public void validate(ModelClassMetaData meta) {
        checkOrphanedFields(meta);
        checkCrossRefFields(meta);
    }

    protected void checkOrphanedFields(ModelClassMetaData meta) {

        Class<?> modelClass = meta.getClazz();

        for (Field field : modelClass.getDeclaredFields()) {
            ColumnDef columnAnnotation = field.getAnnotation(ColumnDef.class);
            if (columnAnnotation != null) {
                // an annotated column MUST have a getter/setter pair to be handled properly 
                // by the persistence layer.

                String fieldNameCapatalized = StringUtils.capitalize(field.getName());
                try {                    
                    Method setter = modelClass.getDeclaredMethod("set"+fieldNameCapatalized, field.getType());
                    if (setter == null) {
                        throw new PersistException("Failed to locate setter set"+fieldNameCapatalized);
                    }
                    String prefix = field.getType().isAssignableFrom(boolean.class) ? "is" : "get";
                    Method getter = modelClass.getDeclaredMethod(prefix+fieldNameCapatalized);
                    if (!getter.getReturnType().isAssignableFrom(field.getType())) {
                        throw new PersistException("getter has wrong return type. " + getter);
                    }
                } catch (Exception ex) {
                    throw new PersistException("Failed to locate required getter/setter pair for " + 
                            field.getName() + " on model " + modelClass + ". Make sure your model class as the proper getter/setter for @ColumnDef field " + field.getName() + " (" + ex.toString() + ")");
                }
            }
        }
    }

    protected void checkCrossRefFields(ModelClassMetaData meta) {
        Class<?> modelClass = meta.getClazz();

        for (Field field : modelClass.getDeclaredFields()) {        

            ColumnDef columnAnnotation = field.getAnnotation(ColumnDef.class);
            if (columnAnnotation != null) {
                if (field.getType().isAssignableFrom(Money.class)) {
                    if (StringUtils.isEmpty(columnAnnotation.crossReference()) 
                            && columnAnnotation.crossReferences().length == 0) {
                        throw new PersistException("columns of Money type require a ColumnDef with crossReference, "
                                + "such as @ColumnDef(crossReference=\"isoCurrencyCode\"). see " + field.getName() + " on model " + modelClass  );
                    }
                }
                
                if (!StringUtils.isEmpty(columnAnnotation.crossReference())) {
                    Field crossReferenceField = null;
                    try {
                        crossReferenceField = modelClass.getDeclaredField(columnAnnotation.crossReference());
                    } catch (NoSuchFieldException e) {
                    }
                    if (crossReferenceField == null) {
                        throw new PersistException("No matching field found for ColumnDef crossReference=\"" + columnAnnotation.crossReference() + 
                                "\" see the \"" + field.getName() + "\" field on model " + modelClass);
                    }
                }
            }
        }    
    }
}
    
