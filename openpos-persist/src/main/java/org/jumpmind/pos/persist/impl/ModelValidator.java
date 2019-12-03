package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.IIndex;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.CompositeDef;
import org.jumpmind.pos.persist.PersistException;

public class ModelValidator {

    public static void validate(ModelClassMetaData meta) {
        checkOrphanedFields(meta);
        checkCrossRefFields(meta);
    }

    protected static void checkOrphanedFields(ModelClassMetaData meta) {

        List<Class<?>> compositeDefClasses;
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

    protected static void checkCrossRefFields(ModelClassMetaData meta) {
        Class<?> modelClass = meta.getClazz();
        List<Class<?>> compositeDefClasses = getCompositeDefClasses(modelClass);
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
                    FieldMetaData xRefFieldMeta = meta.getEntityFieldMetaDatas().get(columnAnnotation.crossReference());
                    if (xRefFieldMeta == null) {
                        xRefFieldMeta = meta.getEntityIdFieldMetaDatas().get(columnAnnotation.crossReference());
                    }
                    if (xRefFieldMeta == null) {
                        throw new PersistException("No matching field found for ColumnDef crossReference=\"" + columnAnnotation.crossReference() + 
                                "\" see the \"" + field.getName() + "\" field on model " + modelClass);
                    }
                }
            }
        }    
    }

    private static Field getCrossReferenceField(Class<?> modelClass, ColumnDef columnAnnotation) throws NoSuchFieldException{
            return modelClass.getDeclaredField(columnAnnotation.crossReference());
    }

    private static List<Class<?>> getCompositeDefClasses(Class<?> clazz) {
        ArrayList<Class<?>> compositeDefClasses = new ArrayList<Class<?>>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            CompositeDef compositeDefAnnotation = field.getAnnotation(CompositeDef.class);
            if (compositeDefAnnotation != null) {
                compositeDefClasses.add(field.getType());
                getCompositeDefClasses(field.getType());
            }
        }
        return compositeDefClasses;
    }
}
    
