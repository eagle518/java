/*
 * Copyright (C) 2015 Peter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.genelet.helper.GeneClass;
import com.genelet.helper.GeneConfig;
import com.genelet.helper.GeneHelp;
import static com.genelet.helper.GeneHelp.chdir;
import static com.genelet.helper.GeneHelp.firstUpper;
import com.genelet.helper.ViewAG;
import com.genelet.helper.ViewJsp;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;



/**
 *
 * @author Peter
 */

public class Help {
    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.io.FileNotFoundException
     * @throws org.kohsuke.args4j.CmdLineException
     */
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException, CmdLineException {
        GeneHelp gh = new GeneHelp();
        CmdLineParser parser = new CmdLineParser(gh);
        parser.parseArgument(args);
        
        if( gh.tables.isEmpty() ) {
            System.err.println("Usage: Help [options] table1 table2 ...\n" +
"        --root    program root, default 'C:\\genejava'\n" +
"        --dbtype  database type 'sqlite' or 'mysql', default 'mysql'\n" +
"        --dbname  database name, mandatory\n" +
"        --dbuser  database username, default ''\n" +
"        --dbpass  database password, default ''\n" +
"        --proj project name, default to 'myproject'\n" +
"        --scri  script name, default to 'myscript'\n" +
"        --force   if to override existing files, default to false\n" +
"        --angular if to include Angular 1.3 files, default to false");
            System.exit(1);
        }
        
        try ( //Class.forName("com.mysql.jdbc.Driver");
                Connection  conn = DriverManager.getConnection(gh.jdbc_name(), gh.dbuser, gh.dbpass)) {
            
            gh.dir_all();
        
            String root = gh.root;
            String proj = gh.proj;
            String scri = gh.scri;
            List<String> tables = gh.tables; 
                    
            GeneConfig cnf = new GeneConfig(gh);
            GeneClass  cls = new GeneClass(proj, scri, tables);
            ViewAG      ag = new ViewAG(proj, scri, tables);
            ViewJsp    jsp = new ViewJsp(proj, scri, tables);
            
            if (chdir(root+"/web")) {
                gh.write_it("index.html", jsp.index());
            }
        
            if (chdir(root+"/web/WEB-INF")) {
                gh.write_it(scri+".json", cnf.config());
                gh.write_it("web.xml", cnf.xml());
            }

            if (chdir(root+"/src/"+proj)) { 
                gh.write_it(firstUpper(proj)+"Filter.java", cls.filter());
                gh.write_it(firstUpper(proj)+"Model.java", cls.model());
                gh.write_it(firstUpper(proj)+"Servlet.java", cls.servlet());
                gh.write_it(firstUpper(proj)+"ServletListener.java", cls.listener());
            }
        
            if (chdir(root+"/web/WEB-INF/views/admin")) {
                gh.write_it("login.html", jsp.login());
            }

            if (chdir(root+"/web") && gh.angular) {
                gh.write_it("index.html", ag.index());
                gh.write_it("init.js", ag.init());
                //gh.write_it("genelet.js", ag.ag_geneletjs());
            }
        
            if (chdir(root+"/web/admin") && gh.angular) {
                gh.write_it("header.html", ag.header(ag.bar()));
                gh.write_it("footer.html", "");
                gh.write_it("login.html", ag.login());
            }
        
            if (chdir(root+"/web/public") && gh.angular) {
                gh.write_it("header.html", ag.header(""));
                gh.write_it("footer.html", "");
            }
        
            if (chdir(root+"/web/public/"+ag.tables.get(0)) && gh.angular) {
                gh.write_it("startnew.html", ag.rolepublic());
            }
        
            int i=0;
            for (String t : tables) {
                List<Object> tmp = gh.obj(conn, t);

                String pk = (String) tmp.get(0);
                String ak = (String) tmp.get(1);
                List<String> nons = (List<String>) tmp.get(2);
                List<String> fields = (List<String>) tmp.get(3);

                System.err.println(t);
                System.err.println(fields);
                        
                if (chdir(root+"/src/"+proj+"/"+t.replace("_",""))) {
                    if (i==0) {
                        gh.write_it("Filter.java", cls.obj_filter(t, pk, nons, "\"groups\", Arrays.asList(\"public\")"));
                    } else {
                        gh.write_it("Filter.java", cls.obj_filter(t, pk, nons, ""));
                    }
                    gh.write_it("Model.java", cls.obj_model(t, pk, ak, fields));
                }

                if (i==0 && chdir(root+"/web/WEB-INF/views/public/"+t.replace("_",""))) {
                    gh.write_it("topics.html", jsp.topics(t, ak, pk, fields));
                }
                                
                if (chdir(root+"/web/WEB-INF/views/admin/"+t.replace("_",""))) {
                    gh.write_it("insert.html", jsp.top()+"<h3>Added</h3>"  +jsp.bottom());
                    gh.write_it("delete.html", jsp.top()+"<h3>Deleted</h3>"+jsp.bottom());
                    gh.write_it("update.html", jsp.top()+"<h3>Updated</h3>"+jsp.bottom());
                    gh.write_it("startnew.html",jsp.startnew(t, pk, nons, fields));
                    gh.write_it("topics.html",    jsp.topics(t, ak, pk, fields));
                    gh.write_it("edit.html",        jsp.edit(t, pk, fields));
                }
            
                if (chdir(root+"/web/admin/"+t) && gh.angular) {
                    gh.write_it("startnew.html", ag.startnew(t, pk, nons, fields));
                    gh.write_it("topics.html",     ag.topics(t, pk, fields));
                    gh.write_it("edit.html",         ag.edit(t, pk, fields));
                }
                
                i++;
            }
            conn.close();
        }
    }
}