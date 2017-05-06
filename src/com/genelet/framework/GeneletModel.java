/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import static java.lang.Math.abs;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Peter
 */

public class GeneletModel extends Crud {
    public Map<String,Object> ARGS;
    public List<Map<String,Object>> LISTS;
    public Map<String,Object> OTHER;
    public Map<String,Object> STORAGE;

    public String SORTBY;
    public String SORTREVERSE;
    public String PAGENO;
    public String ROWCOUNT;
    public String TOTALNO;
    public String MAX_PAGENO;
    
    public int total_force;   
    public String FIELD;
    public String EMPTIES;
   
    public String current_key;
    public List<String> current_keys;
    public String current_id_auto;
    public Map<String,String> key_in;
    
    public Map<String,List<Map<String,Object>>> nextpages;
    public List<String> insert_pars;
    public List<String> edit_pars;
    public List<String> update_pars;
    public List<String> insupd_pars;
    public List<String> topics_pars;
    public Map<String,String> topics_hash;

    public GeneletModel() {
        super();
    }

    public GeneletModel(Connection conn) {
        super(conn);
    }
    
    private List<String> filtered_fields(List<String> pars) {
        if (!this.ARGS.containsKey(FIELD)) { return pars; }

        List<String> out = new ArrayList<>();
        Object field = ARGS.get(FIELD);
        if (field instanceof List) {
            for (Object val0 : (List<Object>) field ) {
                String val = (String) val0;
                for (String v : pars) {
                    if (val.equals(v)) {
                        out.add(v);
                        break;
                    }
                }
            }
        } else {
            String val = (String) field;
            for (String v : pars) {
                if (val.equals(v)) {
                    out.add(v);
                    break;
                }
            }
        } 
        
        return out;
    }
    
    private Map<String,Object> get_fv(List<String> pars) {
        Map<String,Object> field_values = new HashMap<>();
        for (String f :  filtered_fields(pars)) {
            if (ARGS.containsKey(f)) {
                field_values.put(f, ARGS.get(f));
            }
        }
        return field_values;
    }
    
    private List<Object> get_id_val(Map<String,Object> extra) {
        List<Object> val = new ArrayList<>();
        if (current_keys != null && !current_keys.isEmpty()) {
            val.add(current_keys);
            for (String v : current_keys) {
                if (ARGS.containsKey(v)) {
                    val.add(ARGS.get(v));
                } else if (extra.containsKey(v)) {
                    val.add(extra.get(v));
                } else {
                    return null;
                }
            }
            return val;
        }

        val.add(current_key);
        if (ARGS.containsKey(current_key)) {
            val.add(ARGS.get(current_key));
        } else if (extra.containsKey(current_key)) {
            val.add(extra.get(current_key));
        } else {
            return null;
        }
        return val;
    }
    
    private Error set_numbers(Map<String,Object>extra) throws SQLException {
        int pageno = 1;
        if (ARGS.containsKey(PAGENO)) {
            Object o = ARGS.get(PAGENO);
            pageno = (o instanceof String) ? Integer.parseInt((String)o) :  (int) o;
        }
        ARGS.put(PAGENO, pageno);
        int nt;
        if (total_force < -1) {
            nt = abs(total_force);
        } else if (total_force == -1 || !ARGS.containsKey(TOTALNO)) {
            Map<String,Object> hash = new HashMap<>();
            Error err = total_hash(hash, TOTALNO, extra);
            if (err != null) { return err; }
            nt = (int) (long) hash.get(TOTALNO);
        } else {
            Object o = ARGS.get(TOTALNO);
            nt = (o instanceof String) ? Integer.parseInt((String)o) :  (int) o;
        }
        ARGS.put(TOTALNO, nt);
        Object o = ARGS.get(ROWCOUNT);
        int nr = (o instanceof String) ? Integer.parseInt((String)o) :  (int) o;
        ARGS.put(ROWCOUNT, nr);
        ARGS.put(MAX_PAGENO, (nt-1)/nr + 1);
        return null;
    }
    
    public Error topics(List<Map<String,Object>> extras) throws SQLException, Exception { 
        Map<String,Object>extra = new HashMap<>();
        if (!extra.isEmpty()) { extra = (Map<String, Object>) extras.get(0); }
        
        if (total_force != 0 && ARGS.containsKey(ROWCOUNT)) {
            Error err = set_numbers(extra);
            if (err != null) { return err; }
        }

        List<String> fields = filtered_fields(topics_pars);
        LISTS = new ArrayList<>();
        Error err = topics_hash_order(LISTS, fields, get_order_string(), extra);
        if (err != null) { return err; }

        return process_after("topics", extras);
    }
    
    public Error edit(List<Map<String,Object>> extras) throws SQLException, Exception {
        Map<String,Object>extra = new HashMap<>();
        if (!extra.isEmpty()) { extra = (Map<String, Object>) extras.get(0); }
        
        if (ARGS.containsKey("_gid_url")) {
            ARGS.put(this.current_key, ARGS.get("_gid_url"));
        }
        List<Object> val = get_id_val(extra);
        if (val == null) { return new Error("1040"); }
        Object id = val.get(0);
        val.remove(0);

        List<String> fields = filtered_fields(edit_pars);
        if (fields == null) { return new Error("1077"); }

        LISTS = new ArrayList<>();
        Error err = edit_hash(LISTS, fields, id, val, extra);
        if (err != null) { return err; }

        return process_after("edit", extras);
    }
    
    public Error insert(List<Map<String,Object>> extras) throws SQLException, Exception {
        Map<String,Object>extra = new HashMap<>();
        if (!extra.isEmpty()) { extra = (Map<String, Object>) extras.get(0); }
        Map<String, Object> field_values = get_fv(insert_pars);
        if (!extra.isEmpty()) {
            for (String key: extra.keySet()) {
                if (Base.grep(key, insert_pars)) {
                    field_values.put(key, extra.get(key));
                }
            }
        }
        if (field_values.isEmpty()) { return new Error("1078"); }

        Error err = insert_hash(field_values);
        if (err != null) { return err; }

        if (!"".equals(current_id_auto)) {
            field_values.put(current_id_auto, getLast_id());
        }
        LISTS = new ArrayList<>();
        LISTS.add(field_values);

        return process_after("insert", extras);
    }
    
    public Error insupd(List<Map<String,Object>> extras) throws SQLException, Exception {
        Map<String,Object>extra = new HashMap<>();
        if (!extra.isEmpty()) { extra = (Map<String, Object>) extras.get(0); }
        if (insupd_pars.isEmpty()) { return new Error("1078"); }

        Map<String,Object> field_values = get_fv(insert_pars);
        if (!extra.isEmpty()) {
            for (String key: extra.keySet()) {
                if (Base.grep(key, edit_pars)) {
                    field_values.put(key, extra.get(key));
                }
            }
        }
        if (field_values.isEmpty()) { return new Error("1076"); }

        for (String v : insupd_pars) {
            if (!field_values.containsKey(v)) { return new Error("1075"); }
        }

        Map<String,Object> upd_field_values = get_fv(update_pars);
        Error err = insupd_hash(field_values, upd_field_values, current_key, insupd_pars);
        if (err != null) { return err; }

        if (!"".equals(current_id_auto)) {
            field_values.put(current_id_auto, getLast_id());
        }
        LISTS = new ArrayList<>();
        LISTS.add(field_values);
        LISTS.add(upd_field_values);

        return process_after("insupd", extras);
    }
    
    public Error update(List<Map<String,Object>> extras) throws SQLException, Exception {
        Map<String,Object>extra = new HashMap<>();
        if (!extra.isEmpty()) { extra = (Map<String, Object>) extras.get(0); }
        List<Object> val = get_id_val(extra);
        if (val == null) { return new Error("1040"); }
        Object id = val.get(0);
        val.remove(0);

        Map<String,Object> field_values = get_fv(update_pars);
        if (field_values == null) { return new Error("1074"); }

        if (field_values.size()==1 && field_values.containsKey((String)id)) {
            LISTS = new ArrayList<>();
            LISTS.add(field_values);
            return process_after("update", extras);
        }

        if (ARGS.containsKey(EMPTIES)) {
            Error err = update_hash_nulls(field_values, id, val, (List<String>)ARGS.get(EMPTIES), extra);
            if (err != null) { return err; }
        } else {
            Error err = update_hash(field_values, id, val, extra);
            if (err != null) { return err; }
        }

        add_id(id, val, field_values);
  
        return process_after("update", extras);
    }
    
    public Error delete(List<Map<String,Object>> extras) throws SQLException, Exception {
        Map<String,Object>extra = new HashMap<>();
        if (!extra.isEmpty()) { extra = (Map<String, Object>) extras.get(0); }
        List<Object> val = get_id_val(extra);
        if (val == null) { return new Error("1040"); }
        Object id = val.get(0);
        val.remove(0);

        if (key_in != null && !key_in.isEmpty()) {
            for (String table : key_in.keySet()) {
                String keyname = key_in.get(table);
                for (Object v : val) {
                    Error err = existing(table, keyname, (String)v);
                    if (err != null) { return err; }
                }
            }
        }

        Error err = delete_hash(id, val, extra);
        if (err != null) { return err; }
        
        Map<String,Object>field_values = new HashMap<>();
        add_id(id, val, field_values);

        return process_after("delete", extras);
    }
    
    private void add_id(Object id, List<Object> val, Map<String,Object>field_values) {
        if (id instanceof List) {
            List<String> id_list = (List<String>) id; 
            for (int i=0; i<id_list.size(); i++) {
               field_values.put(id_list.get(i), val.get(i));
           } 
        } else {
            field_values.put((String)id, val.get(0));
        }
        LISTS = new ArrayList<>();
        LISTS.add(field_values);
    }
    
    public Error existing(String table, String field, Object val) throws SQLException {
        Map<String,Object> hash = new HashMap<>();
        Error err = get_sql(hash, "SELECT " + field + " FROM " + table + " WHERE " + field + "=?", val);
        if (err != null) { return err; }
        if (hash.isEmpty()) { return new Error("1075"); }
        return null;
    }
    
    public Error randomid(String table, String field, Object... m) throws SQLException {
        int min=0, max=2147483647, trials=10;
        if (m != null && m[0] != null) {min=(int)m[0];}
        if (m != null && m[1] != null) {max=(int)m[1];}
        if (m != null && m[2] != null) {trials=(int)m[2];}
        
        for (int i=0; i<trials; i++) {
            int val = (int) (min + Math.random() * (max-min));
            Error err = existing(table, field, val);
            if (err!=null) { continue; }
            ARGS.put(field, val);
            return null;
        }

        return new Error("1076");
    }
    
    public String get_order_string() {
        String column = ARGS.containsKey(SORTBY) ? (String) ARGS.get(SORTBY) 
                : (current_keys==null||current_keys.isEmpty()) ? current_key 
                : String.join(",", current_keys);

        if (current_keys!=null && !current_tables.isEmpty()) {
            Table t = current_tables.get(0);
            column = (("".equals(t.getAlias())) ? t.getAlias() : t.getName()) + "." + column; 
        }
        
        String order = "ORDER BY " + column;
        if (ARGS.containsKey(SORTREVERSE)) {order += " DESC";}
        if (ARGS.containsKey(ROWCOUNT)) {
            Object o = ARGS.get(ROWCOUNT);
            int rowcount = (o instanceof String) ? Integer.parseInt((String)o) :  (int) o;
            int pageno = 1;
            if (ARGS.containsKey(PAGENO)) {
                Object oo = ARGS.get(PAGENO);
                pageno = (oo instanceof String) ? Integer.parseInt((String)oo) :  (int) oo;
            }
            ARGS.put(PAGENO, pageno);
            ARGS.put(ROWCOUNT, rowcount);
            order += " LIMIT " + Integer.toString(rowcount) + " OFFSET " 
                    + Integer.toString((pageno-1) * rowcount);
        }

        if (order.matches("[;'\"]")) { return ""; }
        return order;
    }

    /**
     * @return the ARGS
     */
    public Map<String,Object> getARGS() {
        return ARGS;
    }

    /**
     * @param ARGS the ARGS to set
     */
    public void setARGS(Map<String,Object> ARGS) {
        this.ARGS = ARGS;
    }

    /**
     * @return the LISTS
     */
    public List<Map<String,Object>> getLISTS() {
        return LISTS;
    }

    /**
     * @param LISTS the LISTS to set
     */
    public void setLISTS(List<Map<String,Object>> LISTS) {
        this.LISTS = LISTS;
    }

    /**
     * @return the OTHER
     */
    public Map<String,Object> getOTHER() {
        return OTHER;
    }

    /**
     * @param OTHER the OTHER to set
     */
    public void setOTHER(Map<String,Object> OTHER) {
        this.OTHER = OTHER;
    }

    /**
     * @return the STORAGE
     */
    public Map<String,Object> getSTORAGE() {
        return STORAGE;
    }

    /**
     * @param STORAGE the STORAGE to set
     */
    public void setSTORAGE(Map<String,Object> STORAGE) {
        this.STORAGE = STORAGE;
    }

    public Error process_after(String action, List<Map<String, Object>> extras) throws IllegalAccessException, Exception {
        if (!extras.isEmpty()) { extras.remove(0); }
        if (nextpages==null || nextpages.isEmpty() || !nextpages.containsKey(action)) { return null; }

        if (OTHER==null) { OTHER = new HashMap<>(); }
        int i=0;
        for (Map<String,Object> page : nextpages.get(action)) {
            Map<String, Object> extra = new HashMap<>();
            if (!extras.isEmpty()) {
                extra = extras.get(0);
                extras.remove(0);
            }
            Error err = (page.containsKey("relate_item"))
                ? call_nextpage(page, extra) : call_once(page, extra);
            if (err != null) { return err; }
        }
        return null;
    }

    private List<Object> another_object(Map<String, Object> page, Map<String, Object> args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception  {
        
        String model = (String) page.get("model");
        Class<?> c = Class.forName(model);
        Object p = c.newInstance();
        Invoke.invokeSet(c, p, "setDBH",  Connection.class, getDBH());
        Invoke.invokeSet(c, p, "setARGS", Map.class,   ARGS);
        String marker = p.getClass().getCanonicalName();
        
        List<Object> ret = new ArrayList<>();
        ret.add(c);
        ret.add(p);
        ret.add(page.get("action"));
        ret.add(marker);
        return ret;
        
    }
    
    public Error call_nextpage(Map<String,Object>page, Map<String,Object>extra) throws Exception {
        if (LISTS == null || LISTS.isEmpty()) { return null; }
        Map<String, Object> next_extra = new HashMap<>();
        if (extra!=null && !extra.isEmpty()) { 
            next_extra = new HashMap<>(extra); 
        }
        if (page.containsKey("manual")) {
            Map<String, Object> manual = (Map<String, Object>) page.get("manual");
            for (String k : manual.keySet()) {
                next_extra.put(k,manual.get(k));
            }
        }
        Map<String,Object> new_ARGS = new HashMap<>(this.ARGS);

        List<Object> ret = another_object(page, new_ARGS);
        Class c = (Class) ret.get(0);
        Object p = ret.get(1);
        String action = (String) ret.get(2);
        String marker = (String) ret.get(3);
        
        for (Map<String, Object> item : LISTS) {
            Map<String, String> pairs = (Map<String, String>) page.get("relate_item");
            for (String k : pairs.keySet()) {
                String v = pairs.get(k);
                if (item.containsKey(k)) {
                    next_extra.put(v, item.get(k));
                } else {
                    next_extra.remove(v);
                }
            }
            
            List<Map<String,Object>> extras = new ArrayList<>();
            Error err = (Error) Invoke.invoke(c, p, action, new Class[]{Map.class, List.class}, new Object[]{next_extra, extras});
            if (err != null) { return err; }
            Object l = Invoke.invokeGet(c, p, "getLISTS");
            if (l!=null) {
                List<Map<String, Object>> lists = (List<Map<String,Object>>) l;
                if (!lists.isEmpty()) { item.put(marker, lists); }
            }
            Object o = Invoke.invokeGet(c, p, "getOTHER");
            if (o!=null) {
                Map<String, Object> other = (Map<String,Object>) o;
                if (!other.isEmpty()) {
                    for (String k : other.keySet()) {
                        OTHER.put(k, other.get(k));
                    }
                }
            }
        }
    
        return null;
    }
        
    public Error call_once(Map<String,Object>page, Map<String,Object>extra) throws InstantiationException, IllegalAccessException, Exception {
        Map<String,Object> new_ARGS = new HashMap<>(this.ARGS);
        List<Object> ret = another_object(page, new_ARGS);
        Class<?> c = (Class) ret.get(0);
        Object p = ret.get(1);
        String action = (String) ret.get(2);
        String marker = (String) ret.get(3);

        if (this.OTHER.containsKey(marker)) { return null; }
        List<Map<String,Object>> extras = new ArrayList<>();
        Error err = (Error) Invoke.invoke(c, p, action, new Class[]{Map.class, List.class}, new Object[]{extra, extras});
        if (err != null) { return err; }
        Object l = Invoke.invokeGet(c, p, "getLISTS");
        if (l!=null) {
            List<Map<String, Object>> lists = (List<Map<String, Object>>) l;
            if (!lists.isEmpty()) { OTHER.put(marker, lists); }
        }
        Object o = Invoke.invokeGet(c, p, "getOTHER");
        if (o!=null) {
            Map<String, Object> other = (Map<String,Object>) o;
            if (!other.isEmpty()) {
                for (String k : other.keySet()) {
                    OTHER.put(k, other.get(k));
                }
            }
        }
        
        return null;
    }
    
}
