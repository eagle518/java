/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Peter
 */
public class Crud extends Dbi {
    protected String current_table;
    protected List<Table> current_tables;

    /**
     *
     */
    public Crud() {
        super();
    }
    
    public Crud(Connection conn) {
        super(conn);
    }
    public Crud(Connection conn, String ct, List<Table> cts) {
        super(conn);
        this.current_table = ct;
        this.current_tables = cts;
    }
    
    public static List<String> select_label_string(Object select_pars) {
        List<String> select_labels = new ArrayList<>();
        if (select_pars instanceof Map) {
            Map<String,String> pars = (Map<String,String>)select_pars;
            String sql = "";
            int i=0;
            for(String key : pars.keySet()) {
                if (i!=0) { sql += ", "; }
                sql += key;
                select_labels.add(pars.get(key));
                i++;
            }
            select_labels.add(0, sql);
        } else if (select_pars instanceof List) {
            for (String v : (List<String>) select_pars) {
                select_labels.add(v);
            }
            select_labels.add(0, String.join(", ", select_labels));
        } else {
            select_labels.add((String)select_pars);
            select_labels.add((String)select_pars);
        }
        
        return select_labels;
    } 
    
    public static List<Object> select_condition_string(Map<String,Object> extra, String... table) {
        if (extra == null || extra.isEmpty()) { return null; }
        
        String sql = "";
        Set<String> fields = extra.keySet();
        
        List<Object> values = new ArrayList<>(); 
        int i=0;
        for (String field : fields) {
            if (i>0) { sql += " AND "; }
            sql += "(";
            Object value = extra.get(field);
            if (table != null && table.length>0 && !"".equals(table[0])) {
                field = table[0] + "." + field;
            }
            if (value instanceof List) {
                List<Object> value_list = (List<Object>) value;
                int n = value_list.size();
                sql += field + " IN (" + Dbi.n_question(n) + ")";
                for (Object v : value_list) {
                    values.add(v);
                }
            } else {
                sql += field + "=?";
                values.add(value);
            }
            sql += ")";
            i++;
        }
        
        values.add(0,sql);
        return values;
    }
    
    public static List<Object> single_condition_string(Object keyname, List<Object> ids, 
            Map<String,Object> extra) {
        String sql;
        List<Object> extra_values = new ArrayList<>();
        
        if (keyname instanceof List) {
            sql = "(";
            int i=0;
            for (String item : (List<String>)keyname) {
                Object val = ids.get(i);
                if (i>0) { sql += " AND "; }
                if (val instanceof List) {
                    List<Object> val_a = (List) val; 
                    int n = val_a.size();
                    sql += item + " IN (" + Dbi.n_question(n) + ")";
                    for (Object v : val_a) {
                        extra_values.add(v);
                    }
                } else {
                    sql += item + "=?";
                    extra_values.add(val);
                }
                i++;
            }
            sql += ")";
        } else {
            int n = ids.size();
            if (n>0) {
                sql = "(" + (String)keyname + " IN (" + Dbi.n_question(n) + "))";
            } else {
                sql = "(" + (String)keyname + "=?)";
            }
            for (Object id : ids) {
                extra_values.add(id);
            }
        }
        
        if (extra != null && !extra.isEmpty()) {
            Set<String> fields = extra.keySet();
            List<Object> selects = select_condition_string(extra, "");
            sql += " AND " + selects.get(0);
            selects.remove(0);
            for (Object v : selects) {
                extra_values.add(v);
            }
        }
        
        extra_values.add(0, sql);
        return extra_values;
    }
    
    public Error insert_hash(Map<String,Object> field_values) throws SQLException {
        return insert_hash_("INSERT", field_values);    
    }
    
    public Error replace_hash(Map<String,Object> field_values) throws SQLException {
        return insert_hash_("REPLACE", field_values);
    }
    
    private Error insert_hash_(String how, Map<String,Object> field_values) 
            throws SQLException {
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Set<String> ks = field_values.keySet();
        for (String k : ks) {
            fields.add(k);
            values.add(field_values.get(k));
        } 
        
        String sql = how + " INTO " + current_table + " (" + String.join(", ", fields) 
                + ") VALUES (" + Dbi.n_question(fields.size()) + ")";
        return do_sql(sql, values.toArray());
    }
    
    public Error update_hash(Map<String,Object> field_values, Object keyname, 
            List<Object> ids, Map<String,Object> extra) throws SQLException {
        return update_hash_nulls(field_values, keyname, ids, null, extra);
    }
    
    public Error update_hash_nulls(Map<String,Object> field_values,
            Object keyname, List<Object> ids, List<String> empties, 
            Map<String,Object> extra) throws SQLException {
        
        List<String> field0 = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Set<String> ks = field_values.keySet();
        for (String k : ks) {
            field0.add(k+"=?");
            values.add(field_values.get(k));
        } 

        String sql = "UPDATE " + current_table + " SET " + String.join(", ", field0);
        if (empties != null) {
            for (String v : empties) {
                if (field_values.get(v) != null) { continue; }
                if (keyname instanceof List) {
                    if (Base.grep(v, (List<String>) keyname)) { continue; }
                } else {
                    if (v.equals((String)keyname) ) { continue; }
                }
                sql += ", v=NULL";
            }
        }
    
        List<Object> extra_values = single_condition_string(keyname, ids, extra);
        String where = (String) extra_values.get(0);
        extra_values.remove(0);
        if (!"".equals(where)) { sql += "\nWHERE " + where; }
        for (Object v : extra_values) {
            values.add(v);
        }
        return do_sql(sql, values.toArray());
    }
    
    public Error insupd_hash(Map<String,Object> field_values, 
            Map<String,Object> upd_field_values, Object keyname, 
            List<String> uniques) throws SQLException {
        String f;
        if (keyname instanceof List) {
            f = String.join(", ", (List<String>) keyname);
        } else {
            f = (String) keyname;
        }
        
        String s = "SELECT " + f + " FROM " + current_table + "\nWHERE ";
        List<Object> v = new ArrayList<>();
        int i=0;
        for (String val : uniques) {
            if (i>0) { s += " AND "; }
            i++;
            s += val + "=?";
            v.add(field_values.get(val));
        }

        List<Map<String,Object>> lists = new ArrayList<>(0);
        Error err = select_sql(lists, s, v.toArray());
        if (err != null) { return err; }
        if (lists.size()>1) { return new Error("1070"); }

        if (lists.size()==1) {
            List<Object> ids = new ArrayList<>();
            for (String u : uniques) {
                ids.add(field_values.get(u));
            }
            err = update_hash(upd_field_values, uniques, ids, null);
        } else {
            err = insert_hash(field_values);
        }
        if (err != null ) { return err; }

        if (keyname instanceof List) {
            for (String k : (List<String>) keyname) {
                field_values.put(k, lists.get(0).get(k));
            }
        } else {
            String k = (String) keyname;
            field_values.put(k, lists.get(0).get(k));  
        }
        
        return null;
    }
    
    public Error delete_hash(Object keyname, List<Object> ids, 
            Map<String,Object> extra) throws SQLException {
        String sql = "DELETE FROM " + current_table;
        List<Object> extra_values = single_condition_string(keyname, ids, extra);
        String where = (String) extra_values.get(0);
        extra_values.remove(0);
        if (!"".equals(where)) { sql += "\nWHERE " + where; }

        return this.do_sql(sql, extra_values.toArray());
    }
    
    public Error edit_hash(List<Map<String,Object>> lists, Object select_pars, 
            Object keyname, List<Object> ids, Map<String,Object> extra) throws SQLException {
        List<String> select_labels = select_label_string(select_pars);
        String sql = select_labels.get(0);
        select_labels.remove(0);
        sql = "SELECT " + sql + "\nFROM " + current_table;
        List<Object> extra_values = single_condition_string(keyname, ids, extra);
        String where = (String)extra_values.get(0);
        extra_values.remove(0);
        if (!"".equals(where)) { sql += "\nWHERE " + where; }

        return select_sql_label(lists, sql, select_labels, extra_values.toArray());
    }
    
    public Error topics_hash_order(List<Map<String,Object>> lists, Object select_pars, 
            String order, Map<String,Object> extra) throws SQLException {
        List<String> select_labels = select_label_string(select_pars);
        String sql = "SELECT " + select_labels.get(0) + "\nFROM ";
        select_labels.remove(0);
        String table = "";
        if (current_tables==null || current_tables.isEmpty()) {
            sql += current_table;
        } else {
            sql += Table.table_string(current_tables);
            table = current_tables.get(0).getAlias();
            if (table == null || "".equals(table)) {
                table = current_tables.get(0).getName();
            }
        }

        if (extra != null && !extra.isEmpty()) {
            List<Object> values = select_condition_string(extra, table);
            String where = (String) values.get(0);
            values.remove(0);
            if (!"".equals(where)) { sql += "\nWHERE " + where; }
            if (!"".equals(order)) { sql += "\n" + order; }
            return select_sql_label(lists, sql, select_labels, values.toArray());
        }
        if (!"".equals(order)) { sql += "\n" + order; }
        return select_sql_label(lists, sql, select_labels);
    }

    public Error topics_hash(List<Map<String,Object>> lists, Object select_pars, 
            Map<String,Object> extra) throws SQLException {
        return topics_hash_order(lists, select_pars, "", extra);
    }
    
    public Error total_hash(Map<String,Object> hash, String label, Map<String,Object> extra) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ";
        String table = "";
        if (current_tables==null || current_tables.isEmpty()) {
            sql += current_table;
        } else {
            sql += Table.table_string(current_tables);
            table = current_tables.get(0).getAlias();
            if (table == null || "".equals(table)) {
                table = current_tables.get(0).getName();
            }
        }

        List<String> l = new ArrayList<>();
        l.add(label);
        if (extra != null && !extra.isEmpty()) {
            List<Object> values = select_condition_string(extra, table);
            String where = (String) values.get(0);
            values.remove(0);
            if (!"".equals(where)) { sql += "\nWHERE " + where; }
            return get_sql_label(hash, sql, l, values.toArray());
        }
        return get_sql_label(hash, sql, l);
    }

    /**
     * @return the current_table
     */
    public String getCurrent_table() {
        return current_table;
    }

    /**
     * @param current_table the current_table to set
     */
    public void setCurrent_table(String current_table) {
        this.current_table = current_table;
    }

    /**
     * @return the current_tables
     */
    public List<Table> getCurrent_tables() {
        return current_tables;
    }

    /**
     * @param current_tables the current_tables to set
     */
    public void setCurrent_tables(List<Table> current_tables) {
        this.current_tables = current_tables;
    }
    
}

