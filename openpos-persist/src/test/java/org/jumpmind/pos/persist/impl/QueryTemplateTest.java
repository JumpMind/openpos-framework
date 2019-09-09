package org.jumpmind.pos.persist.impl;

import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.SqlStatement;
import org.jumpmind.pos.persist.cars.CarModel;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

public class QueryTemplateTest {

    @Test
    public void testTranslateTrueToOne() throws Exception {
        QueryTemplate template = new QueryTemplate();
        template.setSelect("select antique from car");
        template.setWhere("antique=${antique}");
        Map<String,Object> params = new HashMap<>();
        params.put("antique", true);

        SqlStatement sql = template.generateSQL(new Query<CarModel>().result(CarModel.class), params);
        assertNotNull(sql.getValues());
        assertEquals(1, sql.getValues().size());
        assertEquals(1, sql.getValues().get(0));
    }

    @Test
    public void testTranslateFalseToZero() throws Exception {
        QueryTemplate template = new QueryTemplate();
        template.setSelect("select antique from car");
        template.setWhere("antique=${antique}");
        Map<String,Object> params = new HashMap<>();
        params.put("antique", false);

        SqlStatement sql = template.generateSQL(new Query<CarModel>().result(CarModel.class), params);
        assertNotNull(sql.getValues());
        assertEquals(1, sql.getValues().size());
        assertEquals(0, sql.getValues().get(0));
    }


}
