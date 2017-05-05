/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Invoke;
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
public class InvokeJUnitTest {
    
    public InvokeJUnitTest() {
    }
  
    @Test
    public void InvokeTest() throws Exception {        
        String list1 = (String) Invoke.invokeGet("com.genelet.test.ClassInvoke", "class1Method2");
        assertEquals("method 2 of class1: ", "### Class 1, Method2 ###", list1);

        Class<?> c = Class.forName("com.genelet.test.ClassInvoke");
        Object i = c.newInstance();
        Invoke.invokeSet(c, i, "setX", String.class, "123");
        String list2 = (String) Invoke.invokeGet(c, i, "class1Method3");
        assertEquals("setX should reset the value of method3: ", "123", list2);
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
