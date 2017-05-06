/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class Gate extends Access {
    
    public Gate(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
        super(config, request, response, role_value, chartag_value);
    }
    
    public Error verify_cookie(String raw) {
        Role role = get_role();
        if (raw==null || "".equals(raw)) { return new Error("1025"); }
        
        String value = Scoder.decode_scoder(raw, role.getCoding());
        String[] x = value.split("/");
        if (x.length<5) { return new Error("1020"); }
        String ip = x[0];
        String login = x[1];
        String group = x[2];
        String when = x[3];
        String hash = x[4];
        if (!setIp().equals(ip)) { return new Error("1023"); }
        int w = Integer.parseInt(when);
        int request_time = setWhen();
        if (request_time>w) { return new Error("1022"); }
        if (role.getGrouplist() != null && !role.getGrouplist().isEmpty()) {
            Boolean matched = false;
            for (String g : role.getGrouplist()) {
                if (g.equals(group)) { matched=true; break; }
            } 
            if (matched==false) { return new Error("1027"); }
        }
        if (role.getUserlist() != null && !role.getUserlist().isEmpty()) {
            Boolean matched = false;
            for (String u : role.getUserlist()) {
                if (u.equals(login)) { matched=true; break; }
            } 
            if (matched==false) { return new Error("1021"); }
        }
        
        try {
            if (!digest(role.getSecret(), String.join("", ip, login, group, when)).equals(hash)) {
                return new Error("1024");
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        security = new HashMap<>();
        security.put("X-Forwarded-Time", when);
        security.put("X-Forwarded-User", login);
        security.put("X-Forwarded-Group", group);
        int i = 0;
        String[] values = group.split("|");
        for (String attr : role.getAttributes()) {
            if (i==0) {
                security.put(attr, login);
            } else {
                security.put(attr, values[i-1]);
            }
            i++;
        }
        
        return null;
    }
    
    public Error forbid() {
        Role role = get_role();
        Cookie c = get_cookie(role.getSurface());
        String err_str = "1025";
        if (c != null) {
            Error err = verify_cookie(c.getValue());
            if (err == null) { return null; }
            err_str = err.getMessage();
        }
        Chartag chartag = get_chartag();
        if (chartag.getCase()>0) {
            return e200(chartag.getChallenge());
        }
        
        String escaped = "";
        try {
            String q = request.getQueryString();
            if ("".equals(q)) {
                q = request.getRequestURI();
            } else {
                q = request.getRequestURI() + "?" + q;
            }
            escaped = URLEncoder.encode(q, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Gate.class.getName()).log(Level.SEVERE, null, ex);
        }
        //set_cookie_session(config.getGo_probe_name(), request.getRequestURI());
        set_cookie_expire(role.getSurface());
        //set_cookie_session(role.getSurface()+"_",null);
        String redirect = config.getScript_name() + "/" + role_value + "/" + 
                chartag_value + "/" + config.getLogin_name() + "?" + config.getGo_uri_name() +
                "=" + escaped + "&" + config.getGo_err_name() + "=" + err_str + 
                "&" + config.getRole_name() + "=" + role_value + "&" + 
                config.getTag_name() + "=" + chartag_value;
        return e303(redirect);
    }
    
    public Error handler_logout() {
        Role role = get_role();

        set_cookie_expire(role.getSurface());
        //set_cookie_session(role.getSurface()+"_", null);
        //set_cookie_session(config.getGo_probe_name(), null);

        Chartag chartag = get_chartag();
        if (chartag != null && chartag.getCase() > 0) {
            return e200(chartag.call_logout());
        }
        return e303(role.getLogout());
    }
    
    public Error check_static_file() {
        List<List<Object>> statics = config.getStatic();
        for (List<Object> line : statics) {
            Pattern p = (Pattern) line.get(0);
            Matcher m = p.matcher(request.getRequestURL());
            if (m.matches()) {
                Map<String,String> form = new HashMap<>();
                int i=0;
                while (m.find()) {
                    String v = (String) line.get(i+1);
                    form.put(v, m.group());
                }
                role_value = form.get(config.getRole_name());
                chartag_value = form.get(config.getTag_name());
                if (role_value.equals(config.getPubrole())) { return null; }
                Error err = forbid();
                if (err != null) { return err; }
            }
        }
        return null;
    }
}
