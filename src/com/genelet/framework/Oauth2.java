/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class Oauth2 extends Procedure {
    Map default_pars;

    public Oauth2(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
        super(config, request, response, role_value, chartag_value);
    }
    
    @Override
    public Error authenticate(String login, String password, String url) {
        return null;
    }
}
