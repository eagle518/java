/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class Ticket extends Access {
    public String uri;
    private String provider;
    protected Map out_hash;

    public Ticket(Config config, HttpServletRequest request, HttpServletResponse response, String role_value, String chartag_value) {
        super(config, request, response, role_value, chartag_value);
        uri = request.getParameter(config.getGo_uri_name());
    }

    public Issuer get_issuer() {
        return (Issuer) get_role().getIssuers().get(getProvider());
    }

    public Error handler() {
        //Cookie found = get_cookie(config.getGo_probe_name());
        Cookie found = get_cookie("JSESSIONID");
        if (getUri()==null || "".equals(getUri())) {
            if (found == null) { return new Error("404"); }
            setUri(found.getValue());
        }

        Role role = get_role();
        Issuer issuer = get_issuer();
        System.err.println(issuer.getCredential());
        if (issuer != null && issuer.getCredential().size() > 2) {
            if (request.getParameter(issuer.getCredential().get(2))!=null) {
                return handler_login();
            }
        }

        if (found == null) {
            return login_page(1036);
        }

        String errstr = request.getParameter(config.getGo_err_name());
        if (errstr != null) {
            return login_page(Integer.parseInt(errstr));
        }
        return handler_login();
    }
    
    public Error handler_login() {
        Role role = get_role();
        Issuer issuer = get_issuer();
        /*
        String passin = request.getParameter(role.getSurface());
        if ( passin!=null && (role.getSurface().equals(issuer.getCredential().get(3)))) {
            Error err = verify_cookie(passin);
            if (err != null) {
                return login_page(Integer.parseInt(err.toString()));
            } else {
                set_cookie(role.getSurface(), passin, role.getMax_age());
                set_cookie_session(role.getSurface()+"_", passin);
                return e303(uri);
            }
        }
        */

        String login    = request.getParameter(issuer.getCredential().get(0));
        String password = request.getParameter(issuer.getCredential().get(1));

        Error err = authenticate(login, password, getUri());
        if (err != null) {
            if (Integer.parseInt(err.getMessage()) < 1000) {
                return err;
            } else {
                return login_page(Integer.parseInt(err.getMessage()));
            }
        }

        return handler_fields();
    }
    
    public Error handler_fields() {
        Role role = get_role();
        List<String> fields = new ArrayList<>();
        for (String v : role.getAttributes()) {
            fields.add((String) out_hash.get(v));
        }

        String signed = signature(fields);
        set_cookie(role.getSurface(), signed, role.getMax_age());
        //set_cookie_session(role.getSurface()+"_", signed);

        Chartag chartag = get_chartag();
        if (chartag.getCase() > 0) {
            return e200(chartag.call_logged());
        }
        return e303(getUri());
    }
    
    public Error authenticate(String login, String password, String url) {
        if (login==null || "".equals(login) || password==null || "".equals(password)) { return new Error("1037"); }
        Issuer issuer = get_issuer();
        if (!login.equals(issuer.getProvider_pars().get("Def_login")) || !password.equals(issuer.getProvider_pars().get("Def_password"))) {
            return new Error("1031");
        }

        Role role = get_role();
        out_hash = new HashMap<>();
        out_hash.put("login", issuer.getProvider_pars().get("Def_login"));
        out_hash.put("provider", getProvider());
        return null;
    }

    public Error login_page(int error_code) {
        Issuer issuer = get_issuer();
        Chartag chartag = get_chartag();
        String err_string;
        if (chartag.getCase() > 0) {
            try {
                response.setHeader("Content-Type", chartag.getContent_type());
                response.getWriter().print(chartag.call_failed());
            } catch (IOException ex) {
                Logger.getLogger(Ticket.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        
        request.setAttribute("error_code", Integer.toString(error_code));
        request.setAttribute("Login_name", config.getLogin_name());
        request.setAttribute("Go_uri_name", config.getGo_uri_name());
        request.setAttribute("go_uri", uri);
        request.setAttribute("Login_field", issuer.getCredential().get(0));
        request.setAttribute("Password_field", issuer.getCredential().get(1));
        
        RequestDispatcher dispatcher; 
        dispatcher = request.getServletContext().getRequestDispatcher(config.getTemplate()+"/"+role_value+"/"+config.getLogin_name()+"."+chartag_value);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    
}
