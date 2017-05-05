/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class Base {
    public Config config;
    public HttpServletRequest request;
    public HttpServletResponse response;
    public String role_value;
    public String chartag_value;
    public Map<String, String> security;
    
    public Base() {
        this.config = null;
        this.request = null;
        this.response = null;
        this.security = null;
        this.role_value = "";
        this.chartag_value = "";
    }
    
    public Base(Config config, HttpServletRequest request, HttpServletResponse response) {
        this.config = config;
        this.request = request;
        this.response = response;
        this.security = null;
        this.role_value = "";
        this.chartag_value = "";
    }
    
    public Base(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value ) {
        this.config = config;
        this.request = request;
        this.response = response;
        this.security = null;
        this.role_value = role_value;
        this.chartag_value = chartag_value;
    }

    public Role get_role() {
        if ("".equals(role_value)) { return null; }
        return (Role) config.getRoles().get(role_value);
    }
    
    public Chartag get_chartag() {
        if ("".equals(chartag_value)) { return null; }
        return (Chartag) config.getChartags().get(chartag_value);
    }
    
    public String getIp() {
        return request.getRemoteAddr();
    }

    public String setIp() {
        try {
            byte[] four = InetAddress.getByName(getIp()).getAddress();
            return toHexString(four).substring(0, get_role().getLength());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int setWhen() {
        return (int) Instant.now().getEpochSecond();
    }
    
    public String getJsonUrl(String html, String json) {
        String u = request.getRequestURI();
        String q = request.getQueryString();
        String[] ss = u.split("/");
        List<String> news = new ArrayList<String>();
        for (String s: ss) {
            if (s.equals(html)) {
                news.add(json);
            } else {
                news.add(s);
            }
        }
        
        if ("".equals(q)) {
            return String.join("/", news);
        } else {
            return String.join("/", news) + "?" + q;
        }
    }
    
    public Error fulfill() {
        String go_uri = request.getParameter(config.getGo_uri_name());
        role_value = request.getParameter(config.getRole_name());
        chartag_value = request.getParameter(config.getTag_name());
        if (role_value!=null && !"".equals(role_value) && chartag_value!=null && !"".equals(chartag_value)) { return null; }
        try {
            URI new_uri = new URI(go_uri);
            String path = new_uri.getPath();
            int length = config.getScript_name().length();
            if (path.length() > length && path.substring(0,length).equals(config.getScript_name()) ) {
                String[] path_info = path.substring(length+1,(path.length()-1)).split("/");
                role_value = path_info[0];
                chartag_value = path_info[1];
            }
            if ("".equals(role_value)) { return new Error("1401"); }
            if (get_role()==null) { return new Error("1402"); }
        } catch (URISyntaxException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return null;
    }
    
    public String get_provider() {
        Role role = get_role();
        Map<String,Issuer> issuers = role.getIssuers();
        for (String key : issuers.keySet()) {
            if (issuers.get(key).getDefault()) { return key; }
        }
        return "";
    }
    
    public static String toHexString(byte[] bytes) {
	Formatter formatter = new Formatter();	
	for (byte b : bytes) {
            formatter.format("%02x", b);
	}
	return formatter.toString();
    }

    public static String digest64(String key, String message) throws InvalidKeyException, NoSuchAlgorithmException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
	Mac mac = Mac.getInstance("HmacSHA1");
	mac.init(signingKey);
	return Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes()));
    }
    
    public static String digest(String key, String message) throws InvalidKeyException, NoSuchAlgorithmException {
        return digest64(key, message).replace('+', '|').replace('/', '-').replace('=', '_');
    }
    
    public Cookie get_cookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies==null) { return null; }
        for (Cookie cookie : cookies) {
            String c_name = cookie.getName();
            if (c_name.equals(name) || c_name.equals(name+"_")) {return cookie; }
        }
        return null;
    }
    
    public void set_cookie(String name, String value, int max_age) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(max_age);
        String domain = request.getLocalName();
        String path = "/";
        Role role = get_role();
        if (role != null) {
            if (!"".equals(role.getDomain())) {
                domain = role.getDomain();
            }
            if (!"".equals(role.getPath())) {
                path = role.getPath();
            }
        }
        cookie.setDomain(domain);
        cookie.setPath(path);
        response.addCookie(cookie);
    }
    
    public void set_cookie_session(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }
    
    public void set_cookie_expire(String name) {
        set_cookie(name, null, 0);
    }

    public static Boolean grep(String key, List<String>vars) {
        return vars.stream().anyMatch((var) -> (var.equals(key)));
    }
      
            
    public Error e303(String redirect) {
        try {
            response.sendRedirect(redirect);
        } catch (IOException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Error("303");
    }
    
    public Error e200(String body) {
        response.setHeader("Content-Type", get_chartag().getContent_type());
        try {
            response.getWriter().print(body);
        } catch (IOException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Error("200");
    }
    
    public void send_status_page(int status, String body) {
        if (status==303) {
            try {
                response.sendRedirect(body);
            } catch (IOException ex) {
                Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (status==200) {
            try {
                response.getWriter().print(body);
            } catch (IOException ex) {
                Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
