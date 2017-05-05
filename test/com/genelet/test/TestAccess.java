/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Config;
import com.genelet.framework.Gate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class TestAccess extends Gate {

    public TestAccess(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
        super(config, request, response, role_value, chartag_value);
    }
    
    @Override
    public String setIp() {
        return "123.123.123.123";
    }
    
    /**
     *
     * @return
     */
    @Override
    public int setWhen() {
        return 1;
    }
    
}
