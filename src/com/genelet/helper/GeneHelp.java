package com.genelet.helper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class GeneHelp {
    @Option(name="-force", usage="default false")
    public boolean force;
    
    @Option(name="-angular", usage="default false")
    public boolean angular;
        
    @Option(name="-root", usage="program root, default .")
    public String root = "C:\\genejava";
    
    @Option(name="-project", usage="project name, default 'myproject'")
    public String proj = "myproject";
    
    @Option(name="-script", usage="script name, default 'myjava'")
    public String scri = "myscript";
    
    @Option(name="-dbuser", usage="db username, mandatory")
    public String dbuser = "";
   
    @Option(name="-dbpass", usage="db password, mandatory")
    public String dbpass = "";
        
    @Option(name="-dbname", usage="db name, mandatory")
    public String dbname = "";
          
    @Option(name="-dbtype", usage="db type")
    public String dbtype = "mysql";
    
    @Argument
    public List<String> tables = new ArrayList<>();
      
    public String jdbc_name() {
        if ("sqlite".equals(dbtype)) {
            return "jdbc:sqlite:"+dbname;
        } else {
            return "jdbc:mysql://localhost:3306/"+dbname;
        }
    }
    
    Error select_label(Connection conn, List<Map<String,Object>> lists, String sql, List<String> labels) throws SQLException {
        
        PreparedStatement sth = conn.prepareStatement(sql);
        
        ResultSet rs = sth.executeQuery();
        int columnCount = labels.size();
        while (rs.next()) {
            Map<String,Object> obj = new HashMap<>();
            for (int i=1; i<=columnCount; i++) {
                obj.put(labels.get(i-1), rs.getObject(i));
            }
            lists.add(obj);
        }
        
        return null;
    }
    
    public List<Object> obj(Connection conn, String table) throws SQLException {
        if ("sqlite".equals(dbtype)) {
            return sqlite_(conn, table);
        } else {
            return mysql_(conn, table);
        }
    }

    private List<Object> sqlite_(Connection conn, String table) throws SQLException {  
        String pk = "";
        String ak = "";
        List<String>   nons = new ArrayList<>();
        List<String>     uk = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        
        PreparedStatement sth = conn.prepareStatement("PRAGMA table_info("+table+")");
        ResultSet rs = sth.executeQuery();
        while (rs.next()) {
            int Rowid = rs.getInt(1);
            String Field = rs.getString(2);
            String Type = rs.getString(3);
            int Notnull = rs.getInt(4);
            String Default = rs.getString(5);
            int Pri = rs.getInt(6);

            if ("CURRENT_TIMESTAMP".equals(Default)) {
                continue;
            }
 
            if (Pri==1 && "INTEGER".equals(Type)) {
                ak = Field;
                pk = Field;
                continue;
            }

            if (Pri==1) {
                pk = Field;
            }
            if (Notnull==1) {
                nons.add(Field);
            }
            fields.add(Field);
        }

        List<Object> ret = new ArrayList<>();
        ret.add(pk);
        ret.add(ak);
        ret.add(nons);
        ret.add(fields);
        return ret;


    }
    
    private List<Object> mysql_(Connection conn, String table) throws SQLException {  
        List<Map<String,Object>> lists = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        labels.add("Field");
        labels.add("Type");
        labels.add("Null");
        labels.add("Key");
        labels.add("Default");
        labels.add("Extra");
        Error err = select_label(conn, lists, "DESC "+table, labels);
        if (err != null) { return null; }

        String pk = "";
        String ak = "";
        List<String>   nons = new ArrayList<>();
        List<String>     uk = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        for (Map item : lists) {
            String field = (String) item.get("Field");

            if (item.containsKey("Default") && "CURRENT_TIMESTAMP".equals((String)item.get("Default"))) {
                continue;
            }
            if (item.containsKey("Extra") && "auto_increment".equals((String)item.get("Extra"))) {
                ak = field;
                if ("PRI".equals((String)item.get("Key"))) {
                    pk = field;
                }
                continue;
            }

            if (item.containsKey("Key")) {
                String key = (String) item.get("Key");
                if ("PRI".equals(key)) {
                    pk = field;
                }
                if ("UNI".equals(key)) {
                    uk.add(field);
                }
            }
            if ("No".equals((String)item.get("Null"))) {
                nons.add(field);
            }
            fields.add(field);
        }

        if ("".equals(pk) && uk.size()>0) {
            pk = uk.get(0);
        }

        List<Object> ret = new ArrayList<>();
        ret.add(pk);
        ret.add(ak);
        ret.add(nons);
        ret.add(fields);
        return ret;
    }

    public void write_it(String filename, String content) throws UnsupportedEncodingException, FileNotFoundException, IOException {		
	String fn = System.getProperty("user.dir")+"/"+filename;
        File f = new File(fn);
        if (force || !f.exists()) {
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(content);
            }
        }
    }
    
    public void dir_all() {
        List<String> dirs = new ArrayList<>();
        dirs.add(root);
        dirs.add(root+"/src");
 
        dirs.add(root+"/src/"+proj);
        dirs.add(root+"/web");
        dirs.add(root+"/web/admin");
        dirs.add(root+"/web/public");
        dirs.add(root+"/web/public/"+tables.get(0).replace("_",""));
        dirs.add(root+"/web/WEB-INF");
        if (angular) {
            dirs.add(root+"/www");
            dirs.add(root+"/www/admin");
            dirs.add(root+"/www/public");
            dirs.add(root+"/www/public/"+tables.get(0));
        }
        
        for (String v : tables) {
            dirs.add(root+"/src/"+proj+"/"+v.replace("_",""));
            dirs.add(root+"/web/admin/"+v.replace("_",""));
            if (angular) {
                dirs.add(root+"/www/admin/"+v.replace("_",""));
            }
        }
        
        for (String dir : dirs) {
            new File(dir).mkdir();
            System.err.printf("Creating %s\n", dir);
        }
    }
    
    static String nice(String name) {
        String[] names = name.split(" ");
        String str = "";
        for (String v : names) {
            str += firstUpper(v) + " ";
        }
        return str.substring(0, str.length()-1);
    }
    
    public static String firstUpper(String v) {
        return v.substring(0, 1).toUpperCase() + v.substring(1);
    }
       
    static Map<String,String> titles(List<String> fields) {
        int n = 0;
        Map<String, String> ts = new HashMap<>();
        for (String name : fields) {
            if (name.length()>n) {
                n = name.length();
            }
        }

        for (String name : fields) {     
            int i = n-name.length();
            char[] chars = new char[i];
            Arrays.fill(chars, ' ');        
            ts.put(name, new String(chars) + GeneHelp.nice(name));
        }
        return ts;
    }

    public static boolean chdir(String directory_name) {
        boolean result = false;
        File    directory;

        directory = new File(directory_name).getAbsoluteFile();
        if (directory.exists()) {
            result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
        }
        return result;
    }
}
