/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.helper;

/**
 *
 * @author Peter
 */
public class GeneConfig {
    public GeneHelp gh;
    
    public GeneConfig(GeneHelp gh) {
        this.gh = gh;
    }

    public String config() {
        String str = "{" +
"\n\"Document_root\" : \"" + gh.root + "/web\"," +
"\n\"Project_name\"  : \"" + gh.proj + "\"," +
"\n\"Script_name\"   : \"/" + gh.scri + "\"," +
"\n\"Template\"      : \"\"," +
"\n\"Pubrole\"       : \"public\"," +
"\n\"Secret\"        : \"sf09i51jlbnd0324e;fn 34091A3i5i13vtnsdkvn akUIUUIHKJHV\"," +
"\n\"Chartags\"      : {" +
"\n    \"html\" : {" +
"\n        \"Content_type\":\"text/html; charset=\\\"UTF-8\\\"\"," +
"\n        \"Short\":\"html\"" +
"\n    }," +
"\n    \"json\" : {" +
"\n        \"Content_type\":\"application/json; charset=\\\"UTF-8\\\"\"," +
"\n        \"Short\":\"json\"," +
"\n        \"Challenge\":\"challenge\"," +
"\n        \"Logged\":\"logged\"," +
"\n        \"Logout\":\"logout\"," +
"\n        \"Failed\":\"failed\"," +
"\n        \"Case\":1" +
"\n    }" +
"\n}," +
"\n" +
"\n\"Db\" : [\"" + gh.jdbc_name() + "\", \"" + gh.dbuser + "\", \"" + gh.dbpass + "\"]," +
"\n" +
"\n\"Roles\" : {" +
"\n    \"admin\" : {" +
"\n        \"Id_name\" : \"login\"," +
"\n        \"Is_admin\" : true," +
"\n        \"Attributes\" : [\"login\",\"provider\"]," +
"\n        \"Type_id\" : 1," +
"\n        \"Surface\" : \"cadmin\"," +
"\n        \"Domain\" : \"localhost\"," +                
"\n        \"Duration\" : 86400," +
"\n        \"Max_age\"  : 86400," +
"\n        \"Secret\" : \"13123ed%OINK()H%^*&(PIHNdsncxzdlgpwwq;akdsgfhgf f982\"," +
"\n        \"Coding\" : \"(*&*(&(*)sfd fasf 14812 4HJKL BS1312fhdf fd0-41fdf f\"," +
"\n        \"Logout\" : \"/\"," +
"\n        \"Issuers\" : {" +
"\n            \"plain\" : {" +
"\n                \"Default\" : true," +
"\n                \"Credential\" : [\"login\", \"passwd\", \"direct\", \"cadmin\"]," +
"\n                \"Provider_pars\": {\"Def_login\":\"hello\", \"Def_password\":\"world\"}" +
"\n            }" +
"\n        }" +
"\n    }" +
"\n}\n}";
        return str;
    }     
    
    public String xml() {
        String upper = GeneHelp.firstUpper(gh.proj);
        String driver = "com.mysql.jdbc.Driver";
        if ("sqlite".equals(gh.dbtype)) {
            driver = "org.sqlite.JDBC";
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"\n<web-app version=\"3.1\" xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd\">" +
"\n<servlet>" +
"\n    <servlet-name>" + gh.proj + "</servlet-name>" +
"\n    <servlet-class>" + gh.proj + "." + upper+"Servlet</servlet-class>" +
"\n</servlet>" +
"\n<servlet-mapping>" +
"\n    <servlet-name>"+upper+"Servlet</servlet-name>" +
"\n    <url-pattern>/"+gh.scri+"/*</url-pattern>" +
"\n</servlet-mapping>" +
"\n<session-config>" +
"\n    <session-timeout>" +
"\n        30" +
"\n    </session-timeout>" +
"\n</session-config>" +
"\n<listener>" +
"\n    <listener-class>"+gh.proj+"."+upper+"ServletListener</listener-class>" +
"\n</listener>" +
"\n<context-param>" +
"\n    <param-name>config.filename</param-name>" +
"\n    <param-value>" + gh.root + "/web/WEB-INF/"+gh.scri+ ".json</param-value>" +
"\n</context-param>" +
"\n<context-param>" +
"\n    <param-name>jdbc.type</param-name>" +
"\n    <param-value>" + driver + "</param-value>" +
"\n</context-param>" +

"\n</web-app>";
    }
}
