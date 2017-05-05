/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class Procedure extends Ticket {
    private Connection DBH;

    public Procedure(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
        super(config, request, response, role_value, chartag_value);
    }
    
    public Error run_sql(String call_name, Object... in_vals) {
        List<String> out_pars = get_issuer().getOut_pars();
        if (out_pars.isEmpty()) {
            out_pars = get_role().getAttributes();
        }
        out_hash = new HashMap<>();        
        try {                          
            Dbi dbi =  new Dbi();
            dbi.setDBH(DBH);
            Error err = (call_name.toUpperCase().matches("^SELECT (.*)")) ?
                    dbi.get_sql_label(out_hash, call_name, out_pars, in_vals)
                    : dbi.do_proc(out_hash, out_pars, call_name, in_vals);
            if (err != null) { return new Error("1036" + err.toString()); }
        } catch (SQLException ex) {
            Logger.getLogger(Procedure.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (out_hash.isEmpty()) { return new Error("1031"); }
        if (out_hash.get(out_pars.get(0)) == null) { return new Error("1032"); }

        if (!out_hash.containsKey("role_value")) { out_hash.put("role_value", role_value); }
        if (!out_hash.containsKey("chartag_value")) { out_hash.put("chartag_value", chartag_value); }
        if (!out_hash.containsKey("provider")) { out_hash.put("provider", getProvider()); }
            
        return null;
    }
    
    /**
     *
     * @param login
     * @param password
     * @param url
     * @return
     */
    @Override
    public Error authenticate(String login, String password, String url) {
//if ((issuer.Screen & 1) !=0) {in_vals.add(Ip2int(self.Get_ip()))}
//if ((issuer.Screen & 2) !=0) {in_vals.add(self.Get_ua())}
//if ((issuer.Screen & 4) !=0) {in_vals.add(self.Get_referer())}
//if ((issuer.Screen & 8) !=0) {in_vals.add(url)}
        Error err = run_sql(get_issuer().getSql(), login, password);
        if (err != null) { return err; }       
        String first = get_role().getAttributes().get(0);
        System.err.print("first one is ");
        System.err.println(first);
        if (!out_hash.containsKey(first) || out_hash.get(first)==null) {
            return new Error("1032");
        }
        return null;
    }
    
    public Error authenticate_as(String login) {
        Error err = run_sql(get_issuer().getSql_as(), login);
        if (err != null) { return err; }       
        String first = get_role().getAttributes().get(0);
        if (!out_hash.containsKey(first) || out_hash.get(first)==null) {
            return new Error("1032");
        }
        return null;
    }
    
    protected String callback_address(String url) {
        try {
            String port = (request.getServerPort()==80) ? "" : ":"+Integer.toString(request.getServerPort());
            return request.getScheme() + "://" + request.getServerName() + port
                    + config.getScript_name() + "/" + role_value + "/" + getProvider() + "?"
                    + config.getGo_uri_name() + "=" + URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Procedure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    protected Error fill_provider(Map<String,Object> back, String url) {
        Issuer issuer = get_issuer();
        List<Object> in_vals = new ArrayList<>();
        for (String par : issuer.getIn_pars()) {
            if (!back.containsKey(par)) { return new Error("1144"); }
            in_vals.add(back.get(par));
        }
        
        Error err = run_sql(issuer.getSql(), in_vals.toArray());
        if (err != null) { return err; }

        List<String> attrs = get_role().getAttributes();
        for (String attr : attrs) {
            if (!out_hash.containsKey(attr)) {
                if (back.containsKey(attr)) { out_hash.put(attr, back.get(attr)); }
            }
        }
        if (!out_hash.containsKey(attrs.get(0)) || out_hash.get(attrs.get(0))== null) {
            return new Error("1032");
        }
        return null;
    }

    /**
     * @return the DBH
     */
    public Connection getDBH() {
        return DBH;
    }

    /**
     * @param DBH the DBH to set
     */
    public void setDBH(Connection DBH) {
        this.DBH = DBH;
    }
}
