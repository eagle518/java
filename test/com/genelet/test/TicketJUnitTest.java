/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Config;
import com.genelet.framework.Role;
import com.genelet.framework.Ticket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
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
public class TicketJUnitTest {
    Config c;
    Ticket t;
    HttpServletRequest req;
    HttpServletResponse w;
    
    public TicketJUnitTest() {
        try {
            c = new Config("C:\\Users\\greet_000\\Desktop\\golang\\src\\genelet\\peter.conf");
        } catch (IOException ex) {
            Logger.getLogger(ConfigJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        req = createNiceMock(HttpServletRequest.class);
        expect(req.getParameter("go_uri")).andReturn("uri_address").times(1,5);
        expect(req.getParameter("email")).andReturn("hello").times(1,5);
        expect(req.getParameter("passwd")).andReturn("world").times(1,5);
        expect(req.getLocalName()).andReturn("localhost").times(1,5);
        replay(req);
        
        w = createNiceMock(HttpServletResponse.class);
        expect(w.encodeRedirectURL("uri_address")).andReturn("uri_address").times(1,5);
        replay(w);
        
        t = new TestTicket(c,req,w,"m","json");
    
    }
    
    @Test
    public void TicketCheck() {
    
    assertEquals("json log in page of code 1000 ", "{data:\"failed\"}", t.login_page(1001));

    t.chartag_value = "e";
    t.setProvider("db");
    //String login = t.login_page(1001);
    //assertTrue(login.contains("1001") && login.contains("email") && login.contains("passwd"));

    // test authentication
    Error err = t.authenticate("","w","asw");
    assertEquals("return code ", "1037", err.getMessage());
    err = t.authenticate("h","w","asw");
    assertEquals("return code ", "1031", err.getMessage());
    err = t.authenticate("hello","world","asw");
    assertNull("return is null", err);

    err = t.handler_login();
    assertEquals("successful redirect return ", "303", err.getMessage());

    //t.chartag_value = "json";
    //err = t.handler_login();
    //assertEquals("successful json return ", "200", err.getMessage());

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
