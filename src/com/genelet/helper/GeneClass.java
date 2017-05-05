/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.helper;

import java.util.List;

/**
 *
 * @author Peter
 */
public class GeneClass {
    public String proj;
    public String scri;
    public List<String> tables;
    
    public GeneClass(String p, String s, List<String> t) {
        this.proj = p;
        this.scri = s;
        this.tables = t;
    }
    
    public String obj_filter(String table, String pk, List<String> nons, String groups) {   
        String str = "package " + proj + "." + table.replace("_","") + ";" +
"\n" +
"\nimport "+proj+"."+firstUpper()+"Filter;" +
"\nimport java.util.HashMap;" +
"\nimport java.util.List;" +
"\nimport java.util.Map;" +
"\n" +
"\npublic class Filter extends "+firstUpper()+"Filter {" +
"\n   public Filter() {" +
"\n    this.setActions(new HashMap<String, Map<String,List<String>>>(){" +  
"\n        {" +
"\n            put(\"startnew\", new HashMap<String, List<String>>(){" +
"\n                {" +
"\n                    put(\"options\", AL(\"no_db\", \"no_method\"));";
        if (!"".equals(groups)) {
            str += "\n                    put(" + groups + ");";
        }
        str += "" +
"\n                }" +
"\n            });" +
"\n            put(\"insert\", new HashMap<String, List<String>>(){" +
"\n                {" +
"\n                    put(\"validate\", AL(\""+ String.join("\",\"", nons) + "\"));" +
"\n                 }" +
"\n             });" +
"\n            put(\"edit\", new HashMap<String, List<String>>(){" +
"\n                {" +
"\n                    put(\"validate\", AL(\"" + pk + "\"));" +
"\n                }" +
"\n             });" +
"\n            put(\"update\", new HashMap<String, List<String>>(){" +
"\n                {" +
"\n                    put(\"validate\", AL(\"" + pk + "\"));" +
"\n                 }" +
"\n             });" +
"\n            put(\"delete\", new HashMap<String, List<String>>(){" +
"\n                {" +
"\n                    put(\"validate\", AL(\"" + pk + "\"));" +
"\n                }" +
"\n           });" +
"\n            put(\"topics\", new HashMap<>());" +
"\n            put(\"dashboard\", new HashMap<>());" +
"\n        }" +
"\n    });" +
"\n" +    
"\n    this.setFks(new HashMap<>());" +
"\n   }" +
"\n" +
"\n}";
      return str;      
    }             
    
    public String obj_model(String table, String pk,  String ak, List<String> fields) {
        String str = "package " + proj + "." + table.replace("_", "") + ";" +
"\n" +
"\nimport "+proj+"."+firstUpper()+"Model;"+
"\nimport java.sql.SQLException;" +
"\nimport java.util.ArrayList;" +
"\nimport java.util.List;" +
"\nimport java.util.Map;" +
"\n" +
"\npublic class Model extends "+firstUpper()+"Model {" +
"\npublic Model() {" +
"\n        List<String> adds = AL(\"" + String.join("\",\"", fields) + "\");" +
"\n    insert_pars = new ArrayList<>(adds);";
        if ("".equals(ak)) { 
            str += "\n    insert_pars.add(\"" + pk + "\");";
        }
        str += "\n    update_pars = new ArrayList<>(adds);\n    update_pars.add(\"" + pk + "\");" +
"\n    topics_pars = new ArrayList<>(adds);\n    topics_pars.add(\"" + pk + "\");" +
"\n    edit_pars = new ArrayList<>(adds);\n    edit_pars.add(\"" + pk + "\");" +
"\n    current_table  = \"" + table + "\";" +
"\n    current_key    = \"" + pk + "\";" +
"\n    current_id_auto= \"" + pk + "\";" +
"\n}" +
"\n" +
"\n   public Error dashboard(List<Map<String,Object>> extras) throws SQLException, Exception {" +
"\n       return topics(extras);" +
"\n   }" +
"\n" +
"\n}";
        return str;
    }
     
    public String servlet() {
        return "package "+proj+";" + 
"\n" +
"\nimport com.genelet.framework.GeneletServlet;" + 
"\nimport javax.servlet.annotation.WebServlet;" +
"\n" +   
"\n@WebServlet(\"/"+scri+"\")"+ 
"\npublic class "+firstUpper()+"Servlet extends GeneletServlet {" +
"\n}";
    }
    
    public String listener() {
        String str = "package "+proj+";"+
"\n" +
"\nimport javax.servlet.ServletContext;" +
"\nimport javax.servlet.ServletContextEvent;" +
"\nimport javax.servlet.ServletContextListener;" +
"\n\npublic class "+firstUpper()+"ServletListener implements ServletContextListener {" +
"\n@Override" +
"\n  public void contextInitialized(ServletContextEvent event) {" +
"\n    ServletContext sc = event.getServletContext();" +
"\n    System.err.println(sc.getInitParameter(\"config.filename\"));" +
"\n    System.err.println(\"Genelet Server Starts ...\");" +
"\n    sc.setAttribute(\"configfile\", sc.getInitParameter(\"config.filename\"));" +
"\n    sc.setAttribute(\"jdbctype\", sc.getInitParameter(\"jdbc.type\"));" +               
"\n   }" +
"\n" +
"\n   @Override" +
"\n   public void contextDestroyed(ServletContextEvent arg0) {" +
"\n   }" +
"\n}";    
        return str;
    }
     
    public String filter() {
        return "package "+proj+";" +
"\n"+
"\nimport com.genelet.framework.GeneletFilter;" +
"\nimport java.util.ArrayList;" + 
"\nimport java.util.List;" +
"\n" +
"\npublic class "+firstUpper()+"Filter extends GeneletFilter {" +
"\n" + al() +
"\n}";
    }
    
    public String model() {
        return "package "+proj+";"+
"\n" +
"\nimport com.genelet.framework.GeneletModel;" +
"\nimport java.util.ArrayList;" +
"\nimport java.util.List;" +
"\n" +
"\npublic class "+firstUpper()+"Model extends GeneletModel {" +
"\n   public " + firstUpper()+"Model() {" +
"\n    super();" +
"\n    this.SORTBY      =\"sortby\";" +
"\n    this.SORTREVERSE =\"sortreverse\";" +
"\n    this.PAGENO      =\"pageno\";" +
"\n    this.ROWCOUNT    =\"rowcount\";" +
"\n    this.TOTALNO     =\"totalno\";" +
"\n    this.MAX_PAGENO  =\"max_pageno\";" +
"\n    this.FIELD       =\"field\";" +
"\n    this.EMPTIES     =\"empties\";" +
"\n    this.total_force = 0;" +
"\n   }" +
"\n" + al() +    
"\n}";
    }

    String al() {
        return "    public static List<String> AL(String ... elements) {" +
"\n       List<String> list = new ArrayList<>();" + 
"\n       for (String element : elements) {" +
"\n           list.add(element);" +
"\n       }" +
"\n       return list;" +
"\n   }";
    }

    String firstUpper() {
        return GeneHelp.firstUpper(proj);
    }
    
}
