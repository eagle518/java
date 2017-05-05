/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Dbi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class DbiJUnitTest {
    Dbi dbi;
    
    public DbiJUnitTest() {
        
    }
    
    @Test
    public void TableTest() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {   
        List<Object> b1 = new ArrayList<>();
        b1.add(114);
        b1.add("peter1");
        b1.add("peter1 title");
        b1.add(3.141592);
        b1.add(22);
        List<Object> b2 = new ArrayList<>();
        b2.add(115);
        b2.add("peter2");
        b2.add("peter2 title");
        b2.add(3.141592);
        b2.add(22);
        List<Object> b3 = new ArrayList<>();
        b3.add(116);
        b3.add("peter3");
        b3.add("peter3 title");
        b3.add(3.141592);
        b3.add(22);
        List<List<Object>> b = new ArrayList<>();
        b.add(b1);
        b.add(b2);
        b.add(b3);

        Error err = dbi.do_sql("delete from books");
        assertNull("clean books", err);
        err = dbi.do_sqls("insert into books (id, author, title, price, qty) values (?,?,?,?,?)", b);
        assertNull("return null", err);

        List<String> labels = new ArrayList<>();
        labels.add("column1");
        labels.add("column2");
        labels.add("column3");

        List<Map<String,Object>> lists = new ArrayList<>();
        String strSelect = "select title, price, qty from books WHERE qty=?";
        List<Object> a = new ArrayList<>();
        a.add(22);
        err = dbi.select_sql_label(lists, strSelect, labels, a.toArray());
        assertNull("return null", err);
        assertEquals("column 1 for row 1 is: ", "peter1 title", lists.get(0).get("column1"));
        assertEquals("column 1 for row 2 is: ", "peter2 title", lists.get(1).get("column1"));
        assertEquals("column 1 for row 3 is: ", "peter3 title", lists.get(2).get("column1"));
    }
    
    @Test
    public void ProcedureTest() throws SQLException {
        List<List<String>> select_labels = new ArrayList<>();

        List<String> label1 = new ArrayList<>();
        label1.add("xxxxxx");
        select_labels.add(label1);

        List<String> label2 = new ArrayList<>();
        label2.add("xx");
        label2.add("yy");
        select_labels.add(label2);

        List<String> label3 = new ArrayList<>();
        label3.add("a");
        label3.add("b");
        label3.add("c");
        label3.add("d");
        select_labels.add(label3);
        List<String> label4 = new ArrayList<>();
        label4.add("aaaa");
        label4.add("bbbb");
        select_labels.add(label4);

        List<Object> a = new ArrayList<>();
        a.add(1);
        a.add(2);

        List<Map<String,Object>> list1 = new ArrayList<>();
        List<Map<String,Object>> list2 = new ArrayList<>();

        Error err = dbi.select_proc_label(list1, "p1", null, a.toArray());
        err = dbi.select_proc_label(list2, "p1", select_labels, a.toArray());

        assertEquals("size of first item ", 1, list1.get(0).size());
        assertEquals("size of second item ", 2, list1.get(1).size());
        assertEquals("size of third item ", 4, list1.get(2).size());
        assertEquals("first item, x=", 1, (int) list1.get(0).get("x"));
        assertEquals("size of first item ", 1, list2.get(0).size());
        assertEquals("size of second item ", 2, list2.get(1).size());
        assertEquals("size of third item ", 4, list2.get(2).size());
        assertEquals("first item, x=", 1, (int) list2.get(0).get("xxxxxx"));

        Map<String,Object> hash = new HashMap<>();
        List<String> names = new ArrayList<>();
        names.add("zzzz");
        names.add("z000");
        err = dbi.do_proc(hash, names, "p2", a.toArray());
        assertEquals("hash value of key zzzz is 1 ", "1", hash.get("zzzz"));
        assertEquals("hash value of key z000 is 3 ", "3", hash.get("z000"));
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
                
        this.dbi = new Dbi(conn);

    }

    @After
    public void tearDown() throws SQLException {
        this.dbi.getDBH().close();
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
