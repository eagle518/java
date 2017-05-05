/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author Peter
 */
public class Dbi {
    
    private Connection DBH;
    private int last_id;
    private int affected;
    
    public Dbi() {
        this.DBH = null;
        this.last_id=0;
        this.affected=0;
    }
    
    public Dbi(Connection DBH) {
        this.DBH = DBH;
        this.last_id = 0;
        this.affected = 0;
    }
    
    static String n_question(int n) {
        char[] chars = new char[n];
        Arrays.fill(chars, '?');
        String s = new String(chars);
        return String.join(",", s.split(""));
    }
                
    public Error do_sql(String sql, Object... args) throws SQLException  {
        PreparedStatement sth = getDBH().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i=0; i<args.length; i++) {
            sth.setObject(i+1, args[i]);
        }
        affected = sth.executeUpdate();
        ResultSet rs = sth.getGeneratedKeys();
        if (rs.next()){
            last_id=rs.getInt(1);
        }
        return null;
    }

    public Error do_sqls(String sql, List<List<Object>> args) throws SQLException {
        PreparedStatement sth = getDBH().prepareStatement(sql);
        for (List<Object> item : args) {
            for (int i=0; i<item.size(); i++) {
                sth.setObject(i+1, item.get(i));
            }
            affected = sth.executeUpdate();
        }

        return null;
    }

    public Error get_sql(Map<String,Object> res, String sql, Object... args) throws SQLException {
        return get_sql_label(res, sql, null, args);
    }
    
    public Error get_sql_label(Map<String,Object> res, String sql, List<String> labels, 
            Object... args) throws SQLException {
        List<Map<String,Object>> lists = new ArrayList<>();
        Error err = select_sql_label(lists, sql, labels, args);
        if (err != null) { return err; }
        Set<String> keys = lists.get(0).keySet();
        for (String key : keys) {
            res.put(key, lists.get(0).get(key));
        }
        return null;
    }


    public Error select_sql(List<Map<String,Object>> lists, String sql, Object... args) throws SQLException {
        return select_sql_label(lists, sql, null, args);
    }

    
    public Error select_sql_label(List<Map<String,Object>> lists, String sql, List<String> labels, Object... args) throws SQLException {

        PreparedStatement sth = getDBH().prepareStatement(sql);
        if (args != null) {
            for (int i=0; i<args.length; i++) {
                sth.setObject(i+1, args[i]);
            }
        }

        ResultSet rs = sth.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount;
        if (labels != null) {
            columnCount = labels.size();
        } else {
            labels = new ArrayList<>();
            columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++ ) {
                labels.add(rsmd.getColumnName(i));
            }
        }

        while (rs.next()) {
            Map<String,Object> obj = new HashMap<>();
            for (int i=1; i<=columnCount; i++) {
                obj.put(labels.get(i-1), rs.getObject(i));
            }
            lists.add(obj);
        }
        
        return null;
    }

    
    public Error do_proc(Map<String,Object> hash, List<String> names, String proc_name, Object... args) throws SQLException {
        return select_do_proc_label(null, hash, names, proc_name, null, args);
    }

    public Error select_proc(List<Map<String,Object>> lists, String proc_name, Object... args) throws SQLException {
        return select_do_proc_label(lists, null, null, proc_name, null, args);
    }

    public Error select_do_proc(List<Map<String,Object>> lists, Map<String,Object> hash, List<String> names, String proc_name, Object... args) throws SQLException {
        return select_do_proc_label(lists, hash, names, proc_name, null, args);
    }

    public Error select_proc_label(List<Map<String,Object>> lists, String proc_name, List<List<String>> select_labels, Object... args) throws SQLException {
        return select_do_proc_label(lists, null, null, proc_name, select_labels, args);
    }

    
    public Error select_do_proc_label(List<Map<String,Object>> lists, Map<String,Object> hash, List<String> names, String proc_name, List<List<String>> select_labels, Object... args) throws SQLException {
        int n_in = 0;
        if (args != null) { n_in = args.length; }
        int n_out = 0;
        if (names != null) { n_out = names.size(); }

        String str = "{ call " + proc_name + "(" + n_question(n_in+n_out) + ")}";
        CallableStatement sth = getDBH().prepareCall(str);
        if (n_in != 0) {
            for (int i=0; i<n_in; i++) {
                sth.setObject(i+1, args[i]);
            }
        }
        boolean hadResults = sth.execute();

        if (lists != null) {
            int k=0;
            while (hadResults) {
                ResultSet rs = sth.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount;
                List<String> labels;
                if (select_labels != null) {
                    labels = select_labels.get(k);
                    columnCount = labels.size();
                } else {
                    labels = new ArrayList<>();
                    columnCount = rsmd.getColumnCount();
                    for (int i = 1; i <= columnCount; i++ ) {
                        labels.add(rsmd.getColumnName(i));
                    }
                }
                while (rs.next()) {
                    Map<String,Object> obj = new HashMap<>();
                    for (int i=1; i<=columnCount; i++) {
                        obj.put(labels.get(i-1), rs.getObject(i));
                    }
                    lists.add(obj);
                }
                hadResults = sth.getMoreResults();
                k++;
            }
        }

        if (n_out != 0) {
            for (int i=0; i<n_out; i++) {
                hash.put(names.get(i), sth.getObject(n_in+i+1));
            }
        }
        return null;
    }

    /**
     * @return the DBH
     */
    public Connection getDBH() {
        return DBH;
    }

    /**
     * @param DBH the DBH to set
     */
    public void setDBH(Connection DBH) {
        this.DBH = DBH;
    }

    /**
     * @return the last_id
     */
    public int getLast_id() {
        return last_id;
    }

    /**
     * @return the affected
     */
    public int getAffected() {
        return affected;
    }
}
