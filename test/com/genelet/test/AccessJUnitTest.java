/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Base;
import com.genelet.framework.Config;
import com.genelet.framework.Gate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import static org.easymock.EasyMock.createMock;
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

public class AccessJUnitTest {
    Config c;
    Base b;
    Gate a;
    HttpServletRequest req;
    
    public AccessJUnitTest() {
        try {
            c = new Config("C:\\Users\\greet_000\\Desktop\\golang\\src\\genelet\\peter.conf");
        } catch (IOException ex) {
            Logger.getLogger(ConfigJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        req = createMock(HttpServletRequest.class);
        b = new Base(c,req,null);
        a = new TestAccess(c, req, null, "m", "json");
    }
    
    @Test
    public void AccessSignTest() {
    
    ArrayList<String> li = new ArrayList<>();
    li.add("x3"); li.add("g2"); li.add("g3"); li.add("g4"); 
    String sig = a.signature(li);
    assertEquals("siganiture is ", "Ec9rwEEzh1/0UTuoE7dvi+lu1yvkF2njRA2K4yYNbq3RoCpjFcfSpG9bL9y8g3I/GrMHP8LemWEuRdDMlns/", sig);
    Error err = a.verify_cookie(sig);
    assertNull("error is null", err);
    }
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
