package org.jumpmind.pos.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.pos.persist.cars.CarModel;
import org.jumpmind.pos.persist.impl.DatabaseSchema;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.jumpmind.pos.persist.impl.DmlTemplates;
import org.jumpmind.pos.persist.model.AugmenterHelper;
import org.jumpmind.pos.persist.model.TagHelper;
import org.jumpmind.properties.TypedProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;

@RunWith(ConcurrentTestRunner.class)
public class DBSessionQueryConcurrentTest {
    

    final static String VIN = "KMHCN46C58U242743";
    
    Query<CarModel> carLookup = new Query<CarModel>().named("carLookup").result(CarModel.class);
    Query<CarModel> carLookup1 = new Query<CarModel>().named("carLookup1").result(CarModel.class);
    Query<CarModel> carLookup2 = new Query<CarModel>().named("carLookup2").result(CarModel.class);
    Query<CarModel> carLookup3 = new Query<CarModel>().named("carLookup3").result(CarModel.class);
    
    @Test
    @ThreadCount(5)
    public void setup() throws Exception {
        
        DatabaseSchema databaseSchema = Mockito.mock(DatabaseSchema.class);
        IDatabasePlatform databasePlatform = Mockito.mock(IDatabasePlatform.class);
        TagHelper tagHelper = Mockito.mock(TagHelper.class);
        AugmenterHelper augmenterHelper = Mockito.mock(AugmenterHelper.class);
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        
        Table table = new Table();

        Mockito.when(ps.getConnection()).thenReturn(connection);
        Mockito.when(databasePlatform.getDataSource()).thenReturn(dataSource);
        Mockito.when(databaseSchema.getTable(CarModel.class, CarModel.class)).thenReturn(table);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement("selec from null c0")).thenReturn(ps);
        Mockito.when(ps.getResultSet()).thenReturn(rs);
        Mockito.when(ps.executeQuery()).thenReturn(rs);

        QueryTemplates queryTemplates = new QueryTemplates();
        DmlTemplates dmlTemplates = new DmlTemplates();

        DBSession db = new DBSession("catalog", "schema", databaseSchema, databasePlatform, new TypedProperties(), queryTemplates, dmlTemplates, tagHelper, augmenterHelper);
        
        for (int i = 0; i < 1000; i++) {            
            db.query(carLookup);
            db.query(carLookup1);
            db.query(carLookup2);
            db.query(carLookup3);
        }
    }        


}
