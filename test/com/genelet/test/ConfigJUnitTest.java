/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Blks;
import com.genelet.framework.Chartag;
import com.genelet.framework.Config;
import com.genelet.framework.Role;
import com.genelet.framework.Issuer;
import java.io.File;
import java.io.IOException;
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
public class ConfigJUnitTest {
    Config c;
    public ConfigJUnitTest() {
        try {
            c = new Config(System.getProperty("user.home")+"/Documents/NetBeansProjects/Genelet/test/com/genelet/test/config.json");
        } catch (IOException ex) {
            Logger.getLogger(ConfigJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void ConfigOpenCheck() {
        assertEquals("Script name:", c.getScript_name(), "bb");
        assertEquals("document root should be aa", c.getDocument_root(), "aa");
        Map<String,Role> roles = c.getRoles();
        Map<String,Chartag> chartags = c.getChartags();
        Blks blks = c.getBlks();
        
        Chartag cha = chartags.get("json");
        assertEquals("challenge should be challenge", cha.call_challenge(), "{data:\"challenge\"}");
    
        Role role = roles.get("m");
        Issuer issuer = (Issuer) role.getIssuers().get("db");
        Map<String,String> provider_pars = issuer.getProvider_pars();
        assertEquals("default login is hello", provider_pars.get("Def_login"), "hello");
        assertEquals("default password is world", provider_pars.get("Def_password"), "world");
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
