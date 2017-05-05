/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Scoder;
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
public class ScoderJUnitTest {
    
    public ScoderJUnitTest() {
    }
    
    @Test
    public void encodeDecodeTest() {
        String CRYPTEXT = "12345678901234567890";
        String text = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

        String encode = Scoder.encode_scoder(text, CRYPTEXT);
        String decode = Scoder.decode_scoder(encode, CRYPTEXT);
        assertEquals("encoded string: ", "ukscLf6PQBEi+Vo9mHPWqRT/Uj/IUrQWeNo+gPxcBsuQWSLvtGVE9XckwY5bEBXkrWSxIpkK61z9sDsHNao/RMF+lQCXAufYsUoDNOP8", encode);
        assertEquals("decoded string: ", text, decode);
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
