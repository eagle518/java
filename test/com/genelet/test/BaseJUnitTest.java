/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import static org.easymock.EasyMock.*;
import com.genelet.framework.Base;
import com.genelet.framework.Config;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
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
public class BaseJUnitTest {
    Config c;
    Base b;
    HttpServletRequest req;
    
    public BaseJUnitTest() {
        try {
            c = new Config(System.getProperty("user.home")+"/Documents/NetBeansProjects/Genelet/test/com/genelet/test/config.json");
        } catch (IOException ex) {
            Logger.getLogger(ConfigJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        req = createMock(HttpServletRequest.class);
        b = new Base(c,req,null);
    }
    
    @Test
    public void BaseShaTest() {
           
        try {
            String d = b.digest64("1234567", "root" + "script" + "tmpl");
            assertEquals("Base64 of 1234567 / rootscripttmpl", d, "v7AeDE9+Z6iXuxheHt2fEfEpejI=");
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(BaseJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        c.setScript_name("/bb");
        assertEquals("Script name:", "/bb", c.getScript_name());
        expect(req.getParameter("go_uri")).andReturn("/bb/m/BBB/CCC?action=x").times(1, 2);
        expect(req.getParameter("role")).andReturn("").times(1, 2);
        expect(req.getParameter("tag")).andReturn("").times(1, 2);
        replay(req);
        Error err =  b.fulfill();

        assertEquals("expect the role to be m", "m", b.role_value);
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
