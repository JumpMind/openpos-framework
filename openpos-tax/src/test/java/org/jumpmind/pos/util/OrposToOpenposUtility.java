package org.jumpmind.pos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import com.mysql.cj.jdbc.Driver;

public class OrposToOpenposUtility {

    public static void main(String[] args) throws Exception {
        DriverManager.registerDriver(new Driver());
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost/registerdb?serverTimezone=UTC",
                    "root", "JSQLM1nd166");
            writeAuthenticationSql(c);
            writeGroupRuleSql(c);
            writeTaxableGroupSql(c);
            writeTaxJurisdictionSql(c);
            writeTaxRateRuleSql(c);
        } catch (Exception e) {
        	System.err.println(e);
        } finally {
        	if (c != null) {
        		c.close();
        	}
        }

         

    }
    
    protected static String escapeApostrophes (String string) {
    	String res = string;
    	int start = 0;
    	if (string == null) {
    		return "";
    	}
    	for (int i = 0; i < string.length(); i++) {
    		if (string.charAt(i) == '\'') {
    			StringBuilder sb = new StringBuilder();
    			sb.append(string.substring(start, i));
    			sb.append("'");
    			sb.append(string.substring(i));
    			res = sb.toString();
    			start = i;	
    		}
    	}
    	return res;
    }


    
    protected static void writeAuthenticationSql (Connection c) throws Exception {
    	try {
    		   Statement stmt = c.createStatement();
               ResultSet rs = stmt.executeQuery("select * from pa_athy_tx");
               File file = new File("./src/test/resources/tax/sql/0.0.1_post_01_auth.sql");
               BufferedWriter out = new BufferedWriter(new FileWriter(file));
               Timestamp timestamp = new Timestamp(System.currentTimeMillis());
               while (rs.next()) {
               	int id = rs.getInt("id_athy_tx");
               	String taxAuth = escapeApostrophes(rs.getString("nm_athy_tx"));
               	int rc = rs.getInt("sc_rnd");
               	double rd = rs.getDouble("qu_dgt_rnd");
               	out.write("INSERT INTO tax_authority (ID, Auth_name, rounding_code, "
               			+ "rounding_digits_quantity, create_time, create_by, last_update_time, last_update_by) "
               			+ "VALUES (" + id + ", '" + taxAuth + "', " + rc + ", " + rd + ", '" + timestamp
               			+ "', 'UTIL', '" + timestamp + "', 'UTIL');\n");
               }
               out.close();
               rs.close();
               stmt.close();
           } catch (Exception e) {
        	   System.err.println(e);
           }
    }
    
    protected static void writeGroupRuleSql (Connection c) throws Exception {
    	try {
    		   Statement stmt = c.createStatement();
               ResultSet rs = stmt.executeQuery("select * from RU_TX_GP");
               File file = new File("./src/test/resources/tax/sql/0.0.1_post_01_gpru.sql");
               BufferedWriter out = new BufferedWriter(new FileWriter(file));
               Timestamp timestamp = new Timestamp(System.currentTimeMillis());
               while (rs.next()) {
               	int id = rs.getInt("ty_tx");
               	int aid = rs.getInt("id_athy_tx");
               	int gid = rs.getInt("id_gp_tx");
               	String rulename = escapeApostrophes(rs.getString("nm_ru_tx"));
               	String desc = escapeApostrophes(rs.getString("de_ru_tx"));
               	int cmpsq = rs.getInt("ai_cmpnd");
               	Boolean grossflag = rs.getBoolean("fl_tx_gs_amt");
               	String calMth = rs.getString("cd_cal_mth");
               	String trusgcd = rs.getString("cd_tx_rt_ru_usg");
               	//TODO tax cycle amount?
               	out.write("INSERT INTO tax_group_rule (ID, Authority_id, group_id, "
               			+ "rule_name, description, compound_sequence_number, tax_on_gross_amount_flag, "
               			+ "calculation_method_code, rate_rule_usage_code, cycle_amount, create_time, "
               			+ "create_by, last_update_time, last_update_by) "
               			+ "VALUES (" + id + ", " + aid + ", " + gid + ", '" + rulename + "', '"
               			+ desc + "', " + cmpsq + ", '" + grossflag + "', '" + calMth + "', '" + trusgcd + "', 0, '"
               			+ timestamp + "', 'UTIL', '" + timestamp + "', 'UTIL');\n");
               }
               out.close();
               rs.close();
               stmt.close();
           } catch (Exception e) {
        	   System.err.println(e);
           }
    }
    
    protected static void writeTaxableGroupSql (Connection c) throws Exception {
    	try {
    		   Statement stmt = c.createStatement();
               ResultSet rs = stmt.executeQuery("select * from CO_GP_TX_ITM");
               File file = new File("./src/test/resources/tax/sql/0.0.1_post_01_txgp.sql");
               BufferedWriter out = new BufferedWriter(new FileWriter(file));
               Timestamp timestamp = new Timestamp(System.currentTimeMillis());
               while (rs.next()) {
               	int id = rs.getInt("ID_GP_TX");
               	String gname = escapeApostrophes(rs.getString("nm_gp_tx"));
               	String desc = escapeApostrophes(rs.getString("de_gp_tx"));
               	String recPrntCode = rs.getString("cd_rcv_prt");
               	
               	out.write("INSERT INTO tax_group (ID, group_name, description, "
               			+ "receipt_print_code, create_time, create_by, "
               			+ "last_update_time, last_update_by) " 
               			+ "VALUES (" + id + ", '" + gname + "', '" + desc + "', '" + recPrntCode + "', '"
               			+ timestamp + "', 'UTIL', '" + timestamp + "', 'UTIL');\n");
               }
               out.close();
               rs.close();
               stmt.close();
           } catch (Exception e) {
        	   System.err.println(e);
           }
    }
    
    protected static void writeTaxJurisdictionSql (Connection c) throws Exception {
    	try {
    			int i = 0;
    			Statement stmt = c.createStatement();
    			ResultSet rs = stmt.executeQuery("select * from CO_TX_JUR_ATHY_LNK");
    			File file = new File("./src/test/resources/tax/sql/0.0.1_post_01_txjr.sql");
    			BufferedWriter out = new BufferedWriter(new FileWriter(file));
    			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    			while (rs.next()) {
    				int id = i++;
    				String geoID = escapeApostrophes(rs.getString("ID_CD_GEO"));
    				String authID = escapeApostrophes(rs.getString("ID_ATHY_TX"));
               	
    				out.write("INSERT INTO tax_jurisdiction (ID, geo_code, authority_id, "
    						+ "create_time, create_by, last_update_time, last_update_by) " 
    						+ "VALUES ('" + id + "', '" + geoID + "', '" + authID + "', '"
    						+ timestamp + "', 'UTIL', '" + timestamp + "', 'UTIL');\n");
               }
               out.close();
               rs.close();
               stmt.close();
           } catch (Exception e) {
        	   System.err.println(e);
           }
    }
    
    protected static void writeTaxRateRuleSql (Connection c) throws Exception {
    	try {
    			Statement stmt = c.createStatement();
    			ResultSet rs = stmt.executeQuery("select * from RU_TX_RT");
    			File file = new File("./src/test/resources/tax/sql/0.0.1_post_01_txrr.sql");
    			BufferedWriter out = new BufferedWriter(new FileWriter(file));
    			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    			while (rs.next()) {
    				int id = rs.getInt("TY_TX");
    				String authID = escapeApostrophes(rs.getString("ID_ATHY_TX"));
    				String groupID = escapeApostrophes(rs.getString("ID_GP_TX"));
    				int rrSeqNum = rs.getInt("ai_tx_rt_ru");
    				int typeCode = rs.getInt("CD_TYP");
    				double minTxAm = rs.getDouble("mo_txbl_min");
    				double maxTxAm = rs.getDouble("mo_txbl_max");
    				double txPrcnt = rs.getDouble("PE_TX");
    				double txAmnt = rs.getDouble("mo_tx");
    				out.write("INSERT INTO tax_rate_rule (ID, authority_id, group_id, rate_rule_sequence_number, type_code, "
    						+ "min_taxable_amount, max_taxable_amount, tax_percent, tax_amount, "
    						+ ""
    						+ "create_time, create_by, last_update_time, last_update_by) " 
    						+ "VALUES ('" + id + "', '" + authID + "', '" + groupID + "', "
    						+ rrSeqNum + ", " + typeCode + ", " + minTxAm + ", " + maxTxAm + ", " + txPrcnt + ", " + txAmnt + ", '"
    						+ timestamp + "', 'UTIL', '" + timestamp + "', 'UTIL');\n");
               }
               out.close();
               rs.close();
               stmt.close();
           } catch (Exception e) {
        	   System.err.println(e);
           }
    }

}
