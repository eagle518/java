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
public class ViewAG {
    public String proj;
    public String scri;
    public List<String> tables;
    
    public ViewAG(String p, String s, List<String> t) {
        this.proj = p;
        this.scri = s;
        this.tables = t;
    }
        
    public String startnew(String table, String pk, List<String> nons, List<String> fields) {
        if (nons.isEmpty()) {
            nons.add(pk);
            for (String v : fields) {
                nons.add(v);
            }
        }
        String str = "<h3>Create New</h3>" +
"\n<form name=" + table.replace("_","") + "_startnew ng-submit=\"" +
"\n$parent.send('admin', '" + table.replace("_","") + "', 'insert', single, {" +
"\n    action:'dashboard'" +
"\n})" +
"\n\"><pre>";
        Map<String, String> ts = GeneHelp.titles(nons);
        for (String val : nons) {
            str += ts.get(val) + ": <input type=text ng-model=\"single." + val + "\" placeholder=\"" + ts.get(val) + "\" />";
        }
        str += "</pre>" +
"\n<div>" +
"\n    <button type=submit>Submit Now</button>" +
"\n</div>" +
"\n" +
"\n</form>";

        return str;
    }
    
    public String edit(String table, String pk, List<String> fields) {
        String str = "<h3>Update Record</h3>" +
"\n<form name=\"" + table.replace("_","") + "_edit\" ng-submit=\"" +
"\n$parent.send('admin', '" + table.replace("_","") + "', 'update', single, {" +
"\n    action:'dashboard'" +
"\n})" +
"\n\">" +
"\n<pre>";
        Map<String, String> ts = GeneHelp.titles(fields);
        for (String val : fields) {
            str += ts.get(val) + ": <input type=text ng-model=\"single." + val + "\" value=\"{{ single." + val + " }}\" />";
        }
        str += "</pre>" +
"\n" +
"\n<button type=submit> Update </button>" +
"\n</form>";
        return str;
    }

    public String topics(String table, String pk, List<String> fields) {
        List<String> news = new ArrayList<>(fields);
        news.add(pk);
        String str = "<h3>List of Records</h3>" +
"\n<table class=\"table table-striped table-condensed\">" +
"\n<thead>" +
"\n<tr>";
        for (String val : news) {
            str += "<th>" + GeneHelp.nice(val) + "</th>";
        }

        str += "<td> </td></tr>" +
"\n</thead>" +
"\n<tbody>" +
"\n<tr ng-repeat=\"item in names.Lists\">";
        for (String val : news) {
            if (pk.equals(val)) {
                str += "<td><a href=\"\" ng-click=\"" +
"\n$parent.go('admin', '" + table.replace("_","") + "', 'edit', {" + pk + ": item." + pk + "}" +
"\n)\">{{ item." + pk + " }}</a></td>";
            } else {
                str += "<td>{{ item." + val + " }}</td>";
            }
        }
        str += "<td><a href=\"\" ng-click=\"" +
"\n$parent.go('admin', '" + table.replace("_","") + "', 'delete', {" + pk + ": item." + pk + "}," +
"\n{operator:'delete',id_name:'" + pk + "'}" +
"\n)\">DEL</a></td>";
        str += "" +
"\n</tr>" +
"\n</tbody>" +
"\n</table>" +
"\n" +
"\n<a href=\"\" ng-click=\"go('admin', '" + table.replace("_","") + "', 'startnew')\">Create New Record</a>";
        return str;
    }

    public String login() {
        String str = "<h3>Error: {{ names.errorstr }}</h3>" +
"\n<FORM name=login ng-init=\"data.direct=1\" ng-submit=\"" +
"\n$parent.login('admin', 'plain', data, {action:'dashboard',component:'" + tables.get(0).replace("_","") + "',role:'admin'})\">" +
"\n<pre>" +
"\n   LOGIN: <INPUT TYPE=\"TEXT\"     ng-model=\"data.login\" />" +
"\nPASSWORD: <INPUT TYPE=\"PASSWORD\" ng-model=\"data.passwd\" />" +
"\n<INPUT TYPE=\"SUBMIT\" value=\" SUBMIT \" />" +
"\n</pre>" +
"\n</FORM>";
        return str;
    }
    
    public String bar() {
        String str = "        <div class=\"collapse navbar-collapse\">" +
"\n          <ul class=\"nav navbar-nav\">";
        for (String v : tables) {
            str += "<li><a href=\"\" ng-click=\"$parent.go('admin', '" + v.replace("_","") + "','dashboard')\">" + GeneHelp.nice(v.replace("_","")) + "</a></li>";
        }
        str += "        <li><a target=\"json\" href=\"{{ names.ARGS._guri_json[0] }}\" target=json>JSON</a></li>" +
"\n            <li><a>Welcome <em>{{ names.ARGS.login[0] }}</em>! You are role <em>{{ names.ARGS._grole[0] }}</em>.</a></li>" +
"\n            <li><a href=\"\" ng-click=\"$parent.go('admin','logout', '', {}, {role:'public',component:'" + tables.get(0).replace("_","") + "',action:'startnew'})\">LOGOUT</a></li>" +
"\n          </ul>" +
"\n        </div>";
        return str;
    }

    public String header(String bar) {
        return "" +
"\n    <div class=\"navbar navbar-inverse navbar-fixed-top\" role=\"navigation\">" +
"\n      <div class=\"container\">" +
"\n        <div class=\"navbar-header\">" +
"\n          <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">" +
"\n            <span class=\"sr-only\">Toggle navigation</span>" +
"\n            <span class=\"icon-bar\"></span>" +
"\n            <span class=\"icon-bar\"></span>" +
"\n            <span class=\"icon-bar\"></span>" +
"\n          </button>" +
"\n          <a class=\"navbar-brand\" href=\"\" ng-click=\"$parent.go('public', '" + tables.get(0).replace("_","") + "', 'startnew')\">HOME</a>" +
"\n        </div>" + bar + 
"\n      </div>" +
"\n    </div>";
    }
          
    public String rolepublic() {
        return "<h3>" + GeneHelp.nice(proj).toUpperCase() + "</h3>" +
"\n<h4><a href=\"\" ng-click=\"$parent.go('admin', '" + tables.get(0).replace("_","") + "', 'dashboard')\">Enter Admin (AngularJS)</a></h4>" +
"\n<h4><a href=\"/" + scri + "/admin/html/" + tables.get(0).replace("_","") + "?action=dashboard\">Enter Admin (Server Side)</a></h4>";
    }

    public String init() {
        return "var GOTO = {" +
"\n    script    : \"/" +scri + "\"," +
"\n    app       : \"" + proj + "_app\"," +
"\n    controller: \"" + proj + "_controller\"," +
"\n    role      : \"public\"," +
"\n    component : \"" + tables.get(0).replace("_","") + "\"," +
"\n    action    : \"startnew\"," +
"\n" +
"\n    html      : \"html\"," +
"\n    json      : \"json\"," +
"\n    header    : \"header\"," +
"\n    footer    : \"footer\"," +
"\n    login     : \"login\"," +
"\n    \"delete\"  : \"delete\"," +
"\n    insert    : \"insert\"," +
"\n    update    : \"update\"," +
"\n    lists     : \"Lists\"," +
"\n    challenge : \"challenge\"," +
"\n    failed    : \"failed\"," +
"\n    logged    : \"logged\"," +
"\n    logout    : \"logout\"" +
"\n};";
    }

    public String index() {
        String external = "";
        return "<!DOCTYPE html>" +
"\n<html lang=\"en\">" +
"\n  <head>" +
"\n    <meta charset=\"utf-8\">" +
"\n    <meta name=\"fragment\" content=\"!\" />" +
"\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
"\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
"\n    <title>" + GeneHelp.nice(proj).toUpperCase() + "</title>" +
"\n    <script src=\"" + external + "/bootstrap/js/jquery-1.11.1.min.js\"></script>" +
"\n    <script src=\"" + external + "/bootstrap/js/bootstrap.min.js\"></script>" +
"\n    <script src=\"" + external + "/bootstrap/js/angular.min.js\"></script>" +
"\n    <script src=\"init.js\"></script>" +
"\n    <script src=\"genelet.js\"></script>" +
"\n    <link href=\"" + external + "/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">" +
"\n    <style>" +
"\n        body { padding-top: 50px; }" +
"\n        .starter-template { padding: 40px 15px; text-align: center; }" +
"\n        .nav, .pagination, .carousel, .panel-title a { cursor: pointer; }" +
"\n    </style>" +
"\n  </head>" +
"\n" +
"\n  <body ng-app=\"" + proj + "_app\" ng-controller=\"" + proj + "_controller\">" +
"\n" +
"\n    <ng-include src=\"partial_header\"></ng-include>" +
"\n" +
"\n    <div class=\"container\">" +
"\n    <ng-include src=\"partial\"></ng-include>" +
"\n    </div>" +
"\n" +
"\n    <ng-include src=\"partial_footer\"></ng-include>" +
"\n" +
"\n  </body>" +
"\n</html>";
    }
    
}
