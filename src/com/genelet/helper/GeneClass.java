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
    
    public String obj_json(String table, String pk, String ak, List<String> nons, List<String> fields, String groups) {   
        String str = "{" +
"\n    \"actions\" : {" +
"\n            \"startnew\" : {\"options\" : [\"no_db\", \"no_method\"]" + groups + "}," +
"\n            \"insert\" : {\"validate\" : [\""+ String.join("\",\"", nons) + "\"]}," +
"\n            \"edit\" : {\"validate\" : [\"" + pk + "\"]}," +
"\n            \"update\" : {\"validate\" : [\"" + pk + "\"]}," +
"\n            \"delete\" : {\"validate\" : [\"" + pk + "\"]}," +       
"\n            \"topics\" : {}" +
"\n    },";
        
        String add = "\"" + String.join("\",\"", fields) + "\"";
        String edit = add + ",\"" + pk + "\"";
        if ("".equals(ak)) {
            add  = edit;
        }

        str += "\n" +
"\n        \"insert_pars\" : [" + add + "]," + 
"\n        \"update_pars\" : [" + edit + "]," +
"\n        \"topics_pars\" : [" + edit + "]," +
"\n        \"edit_pars\" : [" + edit + "]," +                
"\n        \"current_table\" : \"" + table + "\"," +
"\n        \"current_key\" : \"" + pk + "\"";
        if (!("".equals(ak))) {
            str += "," +
"\n        \"current_id_auto\" : \"" + ak + "\"";
        }
        str += "\n" + 
"\n}";
        return str;
    }
    
    public String obj_filter(String table) {
        String str = "package " + proj + "." + table.replace("_","") + ";" +
"\n" +
"\nimport "+proj+"."+firstUpper()+"Filter;" +
"\n" +
"\npublic class Filter extends "+firstUpper()+"Filter {" +
"\n   public Filter(Object item) {" +
"\n        super(item);" +
"\n   }" +
"\n}";
      return str;          
    }
    
    public String obj_model(String table) {
                String str = "package " + proj + "." + table.replace("_","") + ";" +
"\n" +
"\nimport "+proj+"."+firstUpper()+"Model;" +
"\n" +
"\npublic class Model extends "+firstUpper()+"Model {" +
"\n   public Model(Object item) {" +
"\n        super(item);" +
"\n   }" +
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
"\nimport com.genelet.framework.Config;" +
"\nimport java.io.File;" +
"\nimport java.io.IOException;" +
"\nimport java.util.HashMap;" +
"\nimport java.util.Map;" +
"\nimport javax.json.JsonObject;" +
"\nimport java.util.logging.Level;" +
"\nimport java.util.logging.Logger;" +
"\nimport javax.servlet.ServletContext;" +
"\nimport javax.servlet.ServletContextEvent;" +
"\nimport javax.servlet.ServletContextListener;" +
"\n\npublic class "+firstUpper()+"ServletListener implements ServletContextListener {" +
"\n@Override" +
"\n  public void contextInitialized(ServletContextEvent event) {" +
"\n    ServletContext sc = event.getServletContext();" +
"\n    System.err.println(sc.getInitParameter(\"config.filename\"));" +
"\n    String f = (String) sc.getInitParameter(\"config.filename\");" +
"\n    try {" +
"\n        Config config = new Config(f);" +
"\n        sc.setAttribute(\"config\", config);" +
"\n" +        
"\n        Map<String, Object> storage = new HashMap<>();" +
"\n        String doc_root = config.getDocument_root();" +
"\n        doc_root = doc_root.substring(0,doc_root.length()-3) + \"src\";" +
"\n        File folder = new File(doc_root+\"/" + proj + "\");" +
"\n        for (File component : folder.listFiles()) {" +
"\n            if (component.isDirectory()) {" +
"\n                String name = component.getPath()+\"/component.json\";" +
"\n                System.err.println(name);" +
"\n                File var = new File(name);" +
"\n                if (var.exists() && var.isFile()) {" +
"\n                    JsonObject loc = Config.get_json(name);" +
"\n                    storage.put(component.getName(), loc);" +
"\n                }" +
"\n            }" +
"\n        }" +
"\n        sc.setAttribute(\"storage\", storage);" +
"\n    } catch (IOException ex) {" +
"\n        Logger.getLogger(MyprojectServletListener.class.getName()).log(Level.SEVERE, null, ex);" +
"\n    }" +                     
"\n    sc.setAttribute(\"jdbctype\", sc.getInitParameter(\"jdbc.type\"));" + 
"\n    System.err.println(\"Genelet Server Starts ...\");" +
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
"\n" +
"\npublic class "+firstUpper()+"Filter extends GeneletFilter {" +
"\n   public " + firstUpper()+"Filter(Object item) {" +
"\n    super(item);" +
"\n   }" +          
"\n}";
    }
    
    public String model() {
        return "package "+proj+";"+
"\n" +
"\nimport com.genelet.framework.GeneletModel;" +
"\n" +
"\npublic class "+firstUpper()+"Model extends GeneletModel {" +
"\n   public " + firstUpper()+"Model(Object item) {" +
"\n      super(item);" +
"\n   }" +
"\n}";
    }


    String firstUpper() {
        return GeneHelp.firstUpper(proj);
    }
    
}
