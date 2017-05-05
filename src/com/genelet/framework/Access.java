/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class Access extends Base {

    public Access(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
        super(config, request, response, role_value, chartag_value);
    }

    public String signature(List<String> fields) {
        List<String> nf = new ArrayList<>(fields);
        String login = nf.get(0);
        nf.remove(0);
        return sign(login, String.join("|", nf));
    }
    
    private String sign(String login, String group) {
        Role role = get_role();
        String when = Integer.toString(setWhen() + role.getDuration());
        String ip = setIp();
        try {
            String hash = digest(role.getSecret(), String.join("", ip, login, group, when));
            return Scoder.encode_scoder(String.join("/", ip, login, group, when, hash), role.getCoding());
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;    
    }

}


