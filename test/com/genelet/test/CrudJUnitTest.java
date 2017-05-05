/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Crud;
import com.genelet.framework.Table;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter
 */
public class CrudJUnitTest {
    Crud crud;
    
    public CrudJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hugo", "hugo", "12hugo34");                
        this.crud = new Crud(conn);
    }

    @After
    public void tearDown() throws SQLException {
        this.crud.getDBH().close();
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void CrudStringTest() {
        List<Table> current_tables = new ArrayList<>();
        current_tables.add(new Table("user",      "u", "",      "",       ""));
        current_tables.add(new Table("parent",    "p", "INNER", "",       "u.parent_id=p.parent_id"));
        current_tables.add(new Table("education", "e", "LEFT",  "edu_id", ""));
        //System.err.println(current_tables.get(1).getAlias());
        
        assertEquals("table string ", "user u\nINNER JOIN parent p ON (u.parent_id=p.parent_id)\nLEFT JOIN education e USING (edu_id)", Table.table_string(current_tables));
        String select_par =  "firstname";
        List<String> sql_labels = Crud.select_label_string(select_par);
        assertEquals("expect first name ", "firstname", sql_labels.get(0));
        assertEquals("expect label", "firstname", sql_labels.get(1));
        
        List<String> select_pars_arr = new ArrayList<>();
        select_pars_arr.add("firstname");
        select_pars_arr.add("lastname");
        select_pars_arr.add("id");
        sql_labels = Crud.select_label_string(select_pars_arr);
        assertEquals("sql is ", "firstname, lastname, id", sql_labels.get(0));
        assertEquals("first name is ", "firstname", sql_labels.get(1));
        assertEquals("last name is ", "lastname", sql_labels.get(2));
        assertEquals("id is ", "id", sql_labels.get(3));
        
        Map<String,String> select_hash = new HashMap<>();
        select_hash.put("firstname","First");
        select_hash.put("lastname","Last");
        select_hash.put("id","ID");
        sql_labels = Crud.select_label_string(select_hash);
        assertEquals("sql is ", "firstname, id, lastname", sql_labels.get(0));
        assertEquals("first name is ", "First", sql_labels.get(1));
        assertEquals("last name is ", "Last", sql_labels.get(3));
        assertEquals("id is ", "ID", sql_labels.get(2));
        
        Map<String,Object> extra = new HashMap<>();
        extra.put("firstname","Peter");
        List<Object> sql_c = Crud.select_condition_string(extra);        
        assertEquals("sql string: ", "(firstname=?)", (String)sql_c.get(0));
        assertEquals("first value: ", "Peter", (String)sql_c.get(1));
        sql_c = Crud.select_condition_string(extra, "user");        
        assertEquals("sql string: ", "(user.firstname=?)", (String)sql_c.get(0));
        assertEquals("first value: ", "Peter", (String)sql_c.get(1));
        
        extra.put("lastname","Tong");
        List<Integer> ds = new ArrayList<>();
        ds.add(1);
        ds.add(2);
        ds.add(3);
        ds.add(4);
        extra.put("id", ds); 
        sql_c = Crud.select_condition_string(extra);        
        assertEquals("sql string: ", "(firstname=?) AND (id IN (?,?,?,?)) AND (lastname=?)", (String)sql_c.get(0));
        assertEquals("first value: ", "Peter", (String)sql_c.get(1));
        assertEquals("second value: ", 1, (int)sql_c.get(2));
        assertEquals("third value: ", 2, (int)sql_c.get(3));
        assertEquals("last value: ", "Tong", (String)sql_c.get(6));

        List<String> keyname = new ArrayList<>();
        keyname.add("user_id");
        keyname.add("edu_id");
        List<Object> ids = new ArrayList<>();
        List<Integer> id0 = new ArrayList<>();
        List<Integer> id1 = new ArrayList<>();
        id0.add(11);id0.add(22);
        id1.add(33);id1.add(44);id1.add(55);
        ids.add(id0);
        ids.add(id1);
        List<Object> s_arr = Crud.single_condition_string(keyname, ids, extra);
        assertEquals("sql is ", "(user_id IN (?,?) AND edu_id IN (?,?,?)) AND (firstname=?) AND (id IN (?,?,?,?)) AND (lastname=?)", s_arr.get(0));
        assertEquals("first ", 11, (int)s_arr.get(1));
        assertEquals("second ", 22, (int)s_arr.get(2));
        assertEquals("third ", 33, (int)s_arr.get(3));
        assertEquals("fourth ", 44, (int)s_arr.get(4));
        assertEquals("five ", 55, (int)s_arr.get(5));
        assertEquals("six ", "Peter", (String)s_arr.get(6));
    }
    
    @Test
    public void SqlTest() throws SQLException {
        crud.setCurrent_table("atesting");
        Error err = crud.do_sql("DROP TABLE IF EXISTS atesting");
        assertNull("create table", err);
        err = crud.do_sql("CREATE TABLE atesting (id int auto_increment, x varchar(255), y varchar(255), primary key (id))");
        assertNull("create table", err);
        
        Map<String,Object> hash = new HashMap<>();
        hash.put("x","a");
        hash.put("y","b");
        err = crud.insert_hash(hash);
        assertNull("insert data", err);
        hash.put("x","c");
        hash.put("y","d");
        err = crud.insert_hash(hash);
        assertNull("insert data", err);
        
        Map<String,Object> hash1 = new HashMap<>();
        hash1.put("y","z");
        List<Object> y = new ArrayList<>();
        y.add(2);
        err = crud.update_hash(hash1, "id", y, new HashMap<>());
        assertNull("update data", err);
        
        List<Map<String,Object>> lists = new ArrayList<>();        
        List<String> label = new ArrayList<>();
        label.add("id"); label.add("x"); label.add("y");
        err = crud.topics_hash(lists, label, new HashMap<>());
        assertNull("update data", err);
        assertEquals("total records ", 2, lists.size());
        assertEquals("first y ", "b", (String)lists.get(0).get("y"));
        assertEquals("second y ", "z", (String)lists.get(1).get("y"));     
               
        lists.clear();
        err = crud.edit_hash(lists, label, "id", y, new HashMap<>());
        assertNull("edit data", err);
        assertEquals("lists length ", 1, lists.size());
        assertEquals("first in ", "c", (String) lists.get(0).get("x"));
        assertEquals("second in pair", "z", (String) lists.get(0).get("y"));

        Map<String,Object>what = new HashMap<>();
        err = crud.total_hash(what, "total", new HashMap<>());
        assertEquals("total ", 2, (long)what.get("total"));
        
        y.clear();
        y.add(1);
        err = crud.delete_hash("id",y,new HashMap<>());
        assertNull("delete null", err);
        
        lists.clear();
        err = crud.topics_hash(lists, label, new HashMap<>());
        assertNull("update data", err);
        assertEquals("total records ", 1, lists.size());
        assertEquals("first y ", "z", (String)lists.get(0).get("y"));
        assertEquals("second x ", "c", (String)lists.get(0).get("x"));
    }
    
}
