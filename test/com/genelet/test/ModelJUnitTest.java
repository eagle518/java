/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Crud;
import com.genelet.framework.GeneletModel;
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
public class ModelJUnitTest {
    GeneletModel model;
    
    public ModelJUnitTest() {
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
        this.model = new GeneletModel(conn);
        this.model.setCurrent_table("testing");
        this.model.SORTBY        ="sortby";
        this.model.SORTREVERSE   ="sortreverse";
        this.model.PAGENO        ="pageno";
        this.model.ROWCOUNT      ="rowcount";
        this.model.TOTALNO       ="totalno";
        this.model.MAX_PAGENO    ="max_pageno";
        this.model.FIELD         ="field";
        this.model.EMPTIES       ="empties";
    }
    
    @After
    public void tearDown() throws SQLException {
        this.model.getDBH().close();
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void RestTest() throws SQLException, Exception {
        
        model.setCurrent_table("testing");
        Error err = model.do_sql("DROP TABLE IF EXISTS testing");
        assertNull("create table", err);
        err = model.do_sql("CREATE TABLE testing (id int auto_increment, x varchar(255), y varchar(255), primary key (id))");
        assertNull("create table", err);
        
        Map<String,Object> ARGS = new HashMap<>();
        List<Map<String,Object>> LISTS = new ArrayList<>();
        Map<String,Object> OTHER = new HashMap<>();

        Map<String,Object> e = new HashMap<>();
        List<Map<String,Object>> es = new ArrayList<>();
    
        model.current_key = "id";
        model.current_id_auto = "id";
        model.insert_pars = new ArrayList<>();
        model.insert_pars.add("id");
        model.insert_pars.add("x");
        model.insert_pars.add("y");
        model.topics_pars = new ArrayList<>(model.insert_pars);

        ARGS.put("x", "a");
        ARGS.put("y", "b");
        model.setARGS(ARGS);
        
        es.add(0,e);
        
        err = model.insert(es);
        assertNull("insert", err);
        assertEquals("last id", 1, model.getLast_id());
        assertEquals("affected", 1, model.getAffected());

        Map<String,Object> hash = new HashMap<>();
        hash.put("x","c");
        hash.put("y","d");
        //es.remove(0);
        es.add(0,hash);
        err = model.insert(es);
        assertNull("insert", err);
        assertEquals("last id", 2, model.getLast_id());

        //es.remove(0);
        es.add(0,e);
        err = model.topics(es);
        assertNull("update table", err);
        assertEquals("rows", 2, model.getLISTS().size());

        model.update_pars = new ArrayList<>(model.insert_pars);
        model.edit_pars = new ArrayList<>(model.insert_pars);
        ARGS.put("id", 2);
        ARGS.put("x", "c");
        ARGS.put("y", "z");
        err = model.update(es);
        assertNull("update table", err);
        assertEquals("affected", 1, model.getAffected());
        
        err = model.edit(es);
        assertNull("edit table", err);
        List<Map<String,Object>> lists = (List<Map<String,Object>>) model.getLISTS();
        assertEquals("edit list", 1, lists.size());
        assertEquals("first in item", "c", (String)lists.get(0).get("x"));
        assertEquals("second in item", "z", (String)lists.get(0).get("y"));
        
        err = model.topics(es);
        assertNull("topics table", err);
        lists = (List<Map<String,Object>>) model.getLISTS();
        assertEquals("topics list", 2, model.getLISTS().size());
        assertEquals("first id in item", 1, (int)lists.get(0).get("id"));
        assertEquals("first in item", "a", (String)lists.get(0).get("x"));
        assertEquals("second in item", "b", (String)lists.get(0).get("y"));
        
        ARGS.put("id", 1);
        err = model.delete(es);
        assertNull("delete table", err);
        
        err = model.topics(es);
        assertNull("topics table", err);
        lists = (List<Map<String,Object>>) model.getLISTS();
        assertEquals("topics list", 1, model.getLISTS().size());
        assertEquals("first id in item", 2, (int)lists.get(0).get("id"));
        assertEquals("first in item", "c", (String)lists.get(0).get("x"));
        assertEquals("second in item", "z", (String)lists.get(0).get("y"));
 
        err = model.do_sql("truncate table testing");
        ARGS.remove("id");
        model.insert_pars.remove("id");
        for (int i=1; i<100; i++) {
            ARGS.put("x","a");
            ARGS.put("y","b");
            err = model.insert(es);
            assertNull("insert table", err);
            assertEquals("id", i, (int) model.getLISTS().get(0).get("id"));
        }

        for (int i=1; i<100; i++) {
            ARGS.put("id", i);
            ARGS.put("y", "c");
            err = model.update(es);
            assertNull("update table", err);
            assertEquals("id", i, (int) model.getLISTS().get(0).get("id"));
            assertEquals("y value", "c", (String) model.getLISTS().get(0).get("y"));
        }
    
        ARGS.put("rowcount",20);
        model.total_force = -1;
        err = model.topics(es);
        assertNull("update table", err);
        lists = (List<Map<String,Object>>) model.getLISTS();
        assertEquals("total",99,(int)ARGS.get("totalno"));
        assertEquals("rowcount",20,(int)ARGS.get("rowcount"));
        assertEquals("pageno",1,(int)ARGS.get("pageno"));
        assertEquals("max_pageno",5,(int)ARGS.get("max_pageno"));

        for (int i=1; i<=20; i++) {
            assertEquals("id",i,(int)lists.get(i-1).get("id"));
        }

        ARGS.put("pageno",3);
        err = model.topics(es);
        assertNull("update table", err);
        lists = (List<Map<String,Object>>) model.getLISTS();
        for (int i=1; i<=20; i++) {
            assertEquals("id",40+i,(int)lists.get(i-1).get("id"));
        }
    }
}
