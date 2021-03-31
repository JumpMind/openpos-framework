package org.jumpmind.pos.persist.impl;

import org.jumpmind.db.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DmlTemplates extends AbstractSqlTemplates {

    Map<String, Map<String, DmlTemplate>> dmlsByDeviceMode = new HashMap<>();

    public DmlTemplates() {
    }

    public void setDmls(List<DmlTemplate> dmls) {
        setDmls("default", dmls);
    }

    public void setDmls(String deviceMode, List<DmlTemplate> dmls) {
        getDmlTemplateMapForDeviceMode(deviceMode).clear();
        dmlsByDeviceMode.put(deviceMode, buildDmlTemplatesByNameMap(dmls));
    }

    public List<DmlTemplate> getDmls(String deviceMode) {
        List<DmlTemplate> templateList = new ArrayList<>(getDmlTemplateMapForDeviceMode(deviceMode).values());
        return templateList;
    }

    public List<DmlTemplate> getDmls() {
        return getDmls("default");
    }

    public void addDmls(List<DmlTemplate> dmls) {
        addDmls("default", dmls);
    }

    public void addDmls(String deviceMode, List<DmlTemplate> dmls) {
        dmlsByDeviceMode.put(deviceMode, buildDmlTemplatesByNameMap(dmls));
    }

    public DmlTemplate getDmlTemplate(String deviceMode, String templateName) {
        return getDmlTemplateMapForDeviceMode(deviceMode).get(templateName);
    }

    @Override
    public void replaceModelClassNamesWithTableNames(DatabaseSchema dbSchema, List<Class<?>> modelClassList, boolean validateTablesInQueries) {
        for (String deviceMode : dmlsByDeviceMode.keySet()) {
            Map<String, DmlTemplate> dmlTemplateMap = dmlsByDeviceMode.get(deviceMode);
            for (DmlTemplate template : dmlTemplateMap.values()) {
                for (Class<?> modelClass : modelClassList) {
                    String modelClassName = modelClass.getSimpleName();

                    Table regularTable = dbSchema.getTableForDeviceMode("default", modelClass, modelClass);
                    Table shadowTable = dbSchema.getTableForDeviceMode("training", modelClass, modelClass);

                    if (regularTable == null)  {
                        //  For whatever reason, we could not find a table associated with the given model class.
                        continue;

                    } else if (shadowTable == null)  {
                        shadowTable = regularTable;
                    }

                    String regularTableName = regularTable.getName();
                    String shadowTableName = shadowTable.getName();

                    if (validateTablesInQueries) {
                        scanSqlTextForLiteralTableName(template.getDml(), "", template.getName(), regularTableName, shadowTableName, dbSchema.getTablePrefix(), modelClassName);
                    }

                    //  In the case below, we always want the model class, NOT its superclass.
                    String tableName = (deviceMode.equals("training") ? shadowTableName : regularTableName);

                    template.setDml(replaceClassModelNameInSqlText(template.getDml(), modelClassName, tableName));
                }
            }
        }
    }

    @Override
    protected String getTemplateSuffix() {
        return "-dml.yml";
    }

    private Map<String, DmlTemplate> buildDmlTemplatesByNameMap(List<DmlTemplate> templateList) {
        Map<String, DmlTemplate> dmlTemplatesMap = new HashMap<>();
        if (templateList != null) {
            for (DmlTemplate t : templateList)  {
                dmlTemplatesMap.put(t.getName(), t.copy());
            }
        }
        return dmlTemplatesMap;
    }

    private Map<String, DmlTemplate> getDmlTemplateMapForDeviceMode(String deviceMode)  {
        //  Normalize the Device Mode. Currently only "training" and "default" are supported.
        deviceMode = (deviceMode == null ? "default" : (deviceMode.equalsIgnoreCase("training") ? "training" : "default"));
        Map<String, DmlTemplate> queryMap = dmlsByDeviceMode.get(deviceMode);
        return (queryMap == null ? new HashMap<>() : queryMap);
    }

}
