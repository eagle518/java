/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

import com.genelet.framework.Config;
import com.genelet.framework.Issuer;
import com.genelet.framework.Role;
import com.genelet.framework.Ticket;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class TestTicket extends Ticket {

    public TestTicket(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
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
    
    @Override
    public Error authenticate(String login, String password, String url) {
        if ("".equals(login) || "".equals(password)) { return new Error("1037"); }
        Issuer issuer = get_issuer();
        if (!login.equals(issuer.getProvider_pars().get("Def_login")) || !password.equals(issuer.getProvider_pars().get("Def_password"))) {
            return new Error("1031");
        }

        Role role = get_role();
        out_hash = new HashMap<>();
        out_hash.put("login", issuer.getProvider_pars().get("Def_login"));
        out_hash.put("user", "x2");
        return null;
    }
}
