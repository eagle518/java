/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Peter
 */
public class ViewJsp {
    public String proj;
    public String scri;
    public List<String> tables;
    
    public ViewJsp(String p, String s, List<String> t) {
        this.proj = p;
        this.scri = s;
        this.tables = t;
    }
        
    String top0() {
        return "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n" +
"<%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix =\"c\" %>" +
"\n<!doctype html>" +
"\n<html>" +
"\n<head>" +
"\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
"\n<title>Administrative Pages</title>" +
"\n</head>" +
"\n<body>" +
"\n<h2>" + GeneHelp.nice(proj).toUpperCase() + "</h2>" +
"\n";
    }

    public String top() {
        return top0() + 
"\n<h4>Welcome <em>${ARGS.login}</em>! " +
"\nYou are role <em>${ARGS.g_role}</em>." +
"\n<a href='${ARGS.g_json_url}'>JSON View</a>" +
"\n<a href=\"logout\">LOGOUT</a></h4>";
}

    public String bottom() {
        return "</body></html>";
    }
    
    public String index() {
        return top0() + 
                "<h4><a href=\"/" + scri + "/admin/html/" + tables.get(0).replace("_", "") + "?action=topics\">Enter Admin (Server Side)</a></h4>" + 
                bottom();
    }
        
    public String login() {
        String str = "<h3>${error_code}</h3>" +
"\n<FORM METHOD=\"POST\" ACTION=\"${Login_name}\">" +
"\n<INPUT TYPE=\"HIDDEN\" NAME=\"${Go_uri_name}\" VALUE=\"${go_uri}\" />" +
"\n<pre>" +
"\n   Login: <INPUT TYPE=\"TEXT\" NAME=\"${Login_field}\" />" +
"\nPassword: <INPUT TYPE=\"PASSWORD\" NAME=\"${Password_field}\" />" +
"\n<INPUT TYPE=\"SUBMIT\" VALUE=\" Log In \" />" +
"\n</pre>" +
"\n</FORM>";
    return top0()+str+bottom();
}

    public String startnew(String table, String pk, List<String> nons, List<String> fields) {
        if (nons.isEmpty()) {
            nons.add(pk);
            for (String v : fields) {
                nons.add(v);
            }
        }

        String str = "<h3>Create New</h3>" +
"\n<form method=post action=\"" + table.replace("_", "") + "\">" +
"\n<input type=hidden name=action value=\"insert\" />" +
"\n<pre>";
        Map<String, String> ts = GeneHelp.titles(nons);
        for (String val : fields) {
            str += ts.get(val) + ": <input type=text name=\"" + val + "\" />\n";
        }
        str += "</pre>" +
"\n<input type=submit value=\" Submit \" />" +
"\n</form>" +
"\n";
    return top()+str+bottom();
}
    
    public String edit(String table, String pk, List<String> fields) {
        String str = "<h3>Update Record</h3>" +
"\n<form method=post action=\"" + table.replace("_", "") + "\">" +
"\n<input type=hidden name=action value=\"update\" />" +
"\n<input type=hidden name=" + pk + " value=\"${ARGS." + pk + "}\" />" +
"\n<c:forEach var='item' items='${LISTS}'><pre>";
        Map<String, String> ts = GeneHelp.titles(fields);
        for (String val : fields) {
            str += "\n" + ts.get(val) + ": <input type=text name=\"" + val + "\" value=\"${item." + val + "}\" />";
        }
        str += "\n</pre></c:forEach>" +
"\n<input type=submit value=\" Submit \" />" +
"\n</form>" +
"\n";
    return top()+str+bottom();
}

    public String topics(String table, String ak, String pk, List<String> fields) {
        List<String> news = new ArrayList<>(fields);
        if (ak.equals(pk)) {
            news.add(pk);
        }
        String str = "<h3>List of Records</h3>" +
"\n<table>" +
"\n<thead>" + 
"\n<tr>";
        for (String val : news) {
            str += "<th>" + GeneHelp.nice(val) + "</th>";
        }
        str += "</tr>" +
"\n</thead>" +
"\n<tbody><c:forEach var='item' items='${LISTS}'>" +
"\n<tr>";
        for (String val : news) {
            if (pk.equals(val)) {
                str += "\n<td><a href=\"" + table.replace("_", "") + "?action=edit&" + pk + "=${item." + pk + "}\">${item." + pk + "}</a></td>";
            } else {
                str += "\n<td>${item." + val + "}</td>";
            }
        }
        str += "\n<td><a href=\"" + table.replace("_", "") + "?action=delete&" + pk + "=${item." + pk + "}\">DEL</a></td>" +
"\n</tr>" +
"\n</c:forEach></tbody>" +
"\n</table>" + 
"\n<h3><a href=\"" + table.replace("_", "") + "?action=startnew\">Create New</a></h3>";

        return top()+str+bottom();
    }

}
