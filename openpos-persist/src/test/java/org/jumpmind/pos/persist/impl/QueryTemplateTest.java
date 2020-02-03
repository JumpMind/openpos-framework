package org.jumpmind.pos.persist.impl;

import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.SqlStatement;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class QueryTemplateTest {

    private QueryTemplate queryTemplate;

    private Query<Object> query;

    private Map<String, Object> params;

    private static final String DEFAULT_SELECT = "select foo from bar";

    @Before
    public void setup() {
        queryTemplate = new QueryTemplate();
        queryTemplate.setSelect(DEFAULT_SELECT);
        query = new Query<>();
        params = new HashMap<>();
    }

    @Test
    public void generateSql() {
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals(DEFAULT_SELECT, sqlStatement.getSql());
        assertEquals(0, sqlStatement.getParameters().getValues().size());
    }

    @Test
    public void generateSQLWithInClause() {
        queryTemplate.setWhere("baz in ( a, b )");
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE baz in ( a, b )", sqlStatement.getSql());
        assertEquals(0, sqlStatement.getParameters().getValues().size());
    }

    @Test
    public void generateSQLWithInClauseAndParameters() {
        queryTemplate.setWhere("baz in ( ${para} )");
        params.put("para", "test");
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE baz in ( :para )", sqlStatement.getSql());
        assertEquals(1, sqlStatement.getParameters().getValues().size());
        assertEquals("test", sqlStatement.getParameters().getValues().get("para"));
    }

    @Test
    public void generateSQLWithInClauseAndParametersAsList() {
        queryTemplate.setWhere("baz in ( ${para} )");
        params.put("para", Arrays.asList("a", "b"));
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE baz in ( :para )", sqlStatement.getSql());
        assertEquals(1, sqlStatement.getParameters().getValues().size());
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para"));
    }

    @Test
    public void generateSQLWithInClauseAndParametersAsArray() {
        queryTemplate.setWhere("baz in ( ${para} )");
        params.put("para", new String[] { "a", "b"});
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE baz in ( :para )", sqlStatement.getSql());
        assertEquals(1, sqlStatement.getParameters().getValues().size());
        assertArrayEquals(new String[] { "a", "b" }, (String[]) sqlStatement.getParameters().getValues().get("para"));
    }

    @Test
    public void generateSQLWithInClauseAndParametersAsListAndNeedToSplit() {
        queryTemplate.setWhere("baz in ( ${para} )");
        params.put("para", Arrays.asList("a", "b", "c", "d"));
        query.setMaxInParameters(2);
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE (baz in ( :para$0 ) OR baz in ( :para$1 ))", sqlStatement.getSql());
        assertEquals(3, sqlStatement.getParameters().getValues().size());
        assertEquals(Arrays.asList("a", "b", "c", "d"), sqlStatement.getParameters().getValues().get("para"));
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para$0"));
        assertEquals(Arrays.asList("c", "d"), sqlStatement.getParameters().getValues().get("para$1"));
    }

    @Test
    public void generateSQLWithInClauseAndParametersAsArrayAndNeedToSplit() {
        queryTemplate.setWhere("baz in ( ${para} )");
        params.put("para", new String[] { "a", "b", "c", "d" });
        query.setMaxInParameters(2);
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE (baz in ( :para$0 ) OR baz in ( :para$1 ))", sqlStatement.getSql());
        assertEquals(3, sqlStatement.getParameters().getValues().size());
        assertArrayEquals(new String[] { "a", "b", "c", "d" }, (String[]) sqlStatement.getParameters().getValues().get("para"));
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para$0"));
        assertEquals(Arrays.asList("c", "d"), sqlStatement.getParameters().getValues().get("para$1"));
    }

    @Test
    public void generateSQLWithNotInClauseAndParametersAsListAndNeedToSplit() {
        queryTemplate.setWhere("baz NOT IN ( ${para} )");
        params.put("para", Arrays.asList("a", "b", "c", "d"));
        query.setMaxInParameters(2);
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE (baz NOT IN ( :para$0 ) OR baz NOT IN ( :para$1 ))", sqlStatement.getSql());
        assertEquals(3, sqlStatement.getParameters().getValues().size());
        assertEquals(Arrays.asList("a", "b", "c", "d"), sqlStatement.getParameters().getValues().get("para"));
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para$0"));
        assertEquals(Arrays.asList("c", "d"), sqlStatement.getParameters().getValues().get("para$1"));
    }

    @Test
    public void generateSQLWithNotInClauseAndParametersAsArrayAndNeedToSplit() {
        queryTemplate.setWhere("baz NOT IN ( ${para} )");
        params.put("para", new String[] { "a", "b", "c", "d" });
        query.setMaxInParameters(2);
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE (baz NOT IN ( :para$0 ) OR baz NOT IN ( :para$1 ))", sqlStatement.getSql());
        assertEquals(3, sqlStatement.getParameters().getValues().size());
        assertArrayEquals(new String[] { "a", "b", "c", "d" }, (String[]) sqlStatement.getParameters().getValues().get("para"));
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para$0"));
        assertEquals(Arrays.asList("c", "d"), sqlStatement.getParameters().getValues().get("para$1"));
    }

    @Test
    public void generateSQLWithInClauseAndParametersAsListAndNeedToSplitMultiLine() {
        queryTemplate.setWhere("baz in \n( ${para} )");
        params.put("para", Arrays.asList("a", "b", "c", "d"));
        query.setMaxInParameters(2);
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE (baz in \n( :para$0 ) OR baz in \n( :para$1 ))", sqlStatement.getSql());
        assertEquals(3, sqlStatement.getParameters().getValues().size());
        assertEquals(Arrays.asList("a", "b", "c", "d"), sqlStatement.getParameters().getValues().get("para"));
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para$0"));
        assertEquals(Arrays.asList("c", "d"), sqlStatement.getParameters().getValues().get("para$1"));
    }

    @Test
    public void generateSQLWithInClauseAndParametersAsListAndNeedToWithMultiple() {
        queryTemplate.setWhere("baz in ( ${para} ) and waz not in ( ${arap} )");
        params.put("para", Arrays.asList("a", "b", "c", "d"));
        params.put("arap", Arrays.asList("z", "y", "x", "w", "v"));
        query.setMaxInParameters(2);
        SqlStatement sqlStatement = queryTemplate.generateSQL(query, params);
        assertEquals("select foo from bar WHERE (baz in ( :para$0 ) OR baz in ( :para$1 )) and " +
                "(waz not in ( :arap$0 ) OR waz not in ( :arap$1 ) OR waz not in ( :arap$2 ))", sqlStatement.getSql());
        assertEquals(7, sqlStatement.getParameters().getValues().size());
        assertEquals(Arrays.asList("a", "b", "c", "d"), sqlStatement.getParameters().getValues().get("para"));
        assertEquals(Arrays.asList("a", "b"), sqlStatement.getParameters().getValues().get("para$0"));
        assertEquals(Arrays.asList("c", "d"), sqlStatement.getParameters().getValues().get("para$1"));
        assertEquals(Arrays.asList("z", "y", "x", "w", "v"), sqlStatement.getParameters().getValues().get("arap"));
        assertEquals(Arrays.asList("z", "y"), sqlStatement.getParameters().getValues().get("arap$0"));
        assertEquals(Arrays.asList("x", "w"), sqlStatement.getParameters().getValues().get("arap$1"));
        assertEquals(Arrays.asList("v"), sqlStatement.getParameters().getValues().get("arap$2"));
    }

}