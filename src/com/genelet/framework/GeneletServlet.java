package com.genelet.framework; 
  
import com.google.gson.Gson;
import java.io.IOException; 
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;
  
// Specify the name of the servlet and the URL for it 
public class GeneletServlet extends HttpServlet { 
    /* 
     *Processes requests for both HTTP <code>GET</code> and <code>POST</code> 
     * methods. 
     * 
     * @param request servlet request 
     * @param response servlet response 
     * @throws ServletException if a servlet-specific error occurs 
     * @throws IOException if an I/O error occurs 
     */ 
    private void login(String provider, Gate gate) throws SQLException, IOException {
        if (provider==null || "".equals(provider)) {
            //System.err.println("300000");
            provider = gate.get_provider();
            //System.err.println(provider);
            if (provider==null || "".equals(provider)) { gate.response.sendError(404); return; }
        }
        
        Error err = null;     
        if (gate.config.getPlain_provider().equals(provider)) {
            //System.err.println(444444);
            Ticket t = new Ticket(gate.config, gate.request, gate.response, gate.role_value, gate.chartag_value);
            t.setProvider(provider);
            err = t.handler();
        } else {
            Connection dbh = null;
            List<String> db = gate.config.getDb();
            try {
                Class.forName((String) getServletContext().getAttribute("jdbctype"));
                dbh = DriverManager.getConnection(db.get(0), db.get(1), db.get(2));
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            if ("google".equals(provider)) {
                Oauth2 t = new Oauth2(gate.config, gate.request, gate.response, gate.role_value, gate.chartag_value);
                t.setProvider("google");
                t.setDBH(dbh);
                err = t.handler();
            } else {
                Procedure t = new Procedure(gate.config, gate.request, gate.response, gate.role_value, gate.chartag_value);
                t.setProvider(provider);
                t.setDBH(dbh);
                err = t.handler();
            }
            if (dbh != null) { dbh.close(); }
        }
        //if (err != null) { gate.send_status_page(200, err.getMessage()); }
    }
    
    public void processRequest(HttpServletRequest r, HttpServletResponse w) throws ServletException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, Exception { 
        System.err.println("\n\nNew Request: " + r.getRequestURL() + "?" + r.getQueryString());
        //Config c = new Config((String) getServletContext().getAttribute("configfile"));
        Config c = (Config) getServletContext().getAttribute("config");
        Gate gate = new Gate(c, r, w, "", "");

        int length = c.getScript_name().length();
        String URIPath = r.getRequestURI();
        int l_url = URIPath.length();
        if (l_url<=length || !URIPath.substring(0,length+1).equals(c.getScript_name()+"/") ) {
            w.sendError(404); // not serving static files
            return;
        }
        String[] path_info = URIPath.substring(length+1).split("/");
        if (path_info.length == 4 && "GET".equals(r.getMethod())) {
            r.setAttribute("_gid_url", path_info[3]);
        } else if (path_info.length != 3) {
            w.sendError(400); // bad request
        } 
        
        gate.role_value = path_info[0];
        gate.chartag_value = path_info[1];
        Role role = gate.get_role();
        String obj = path_info[2];

        if (role==null && !(path_info[0].equals(c.getPubrole()))) {
            w.sendError(404);
        }
        
        if (obj.equals(c.getLogin_name()) || obj.equals(c.getGoogle_provider())) {
            if (obj.equals(c.getGoogle_provider())) {
                login(obj, gate);
            } else {
                System.err.println("Start login....");
                login(r.getParameter(c.getProvider_name()), gate);
                System.err.println("End login ...");
            }
            return;
        } else if (obj.equals(c.getLogout_name())) {
            System.err.println("Logout " + obj);
            Error err = gate.handler_logout();
            return;
        } else if (!path_info[0].equals(c.getPubrole())) {
            System.err.println("Authenticating " + path_info[0]);
            Error err = gate.forbid();
            if (err != null) { return; }
        }
         
        System.err.println("Start serving...");
        Error err = handle(obj, gate);
        if (err != null) { gate.send_status_page(200, err.getMessage()); }
    }

    private Error handle(String obj, Gate gate) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
        String who = gate.role_value;
        String tag = gate.chartag_value;
        
        Map<String,Object> storage = (HashMap<String,Object>) getServletContext().getAttribute("storage");
        Object c_json = storage.get(obj); 
        
        Class filterClass = Class.forName(gate.config.getProject_name()+"." + obj + ".Filter");
        //Object filter = filterClass.newInstance();
        Object filter = filterClass.getDeclaredConstructor(Object.class).newInstance(c_json);
        System.err.println(gate.config.getProject_name()+"." + obj + ".Filter created");
    
        Map<String,Map<String,List<String>>> actions = (Map<String,Map<String,List<String>>>) Invoke.invokeGet(filterClass, filter, "getActions");
        String action = get_action(gate, actions);
        System.err.println("action is: " + action);
        if ("".equals(action)) { return new Error("404"); }
        Map<String, List<String>> actionHash = actions.get(action);
        
        Map<String,Object> ARGS = new HashMap<>();
        for (Entry entry : gate.request.getParameterMap().entrySet()) {
            String[] var = (String[]) entry.getValue();
            if (var.length>1) {
                ARGS.put((String) entry.getKey(), var);
            } else {
                ARGS.put((String) entry.getKey(), var[0]);
            }
        }
        if (gate.request.getAttribute("_gid_url") != null) {
            ARGS.put("_gid_url", gate.request.getAttribute("_gid_url"));
        }
        
        ARGS.put("g_role", who);
        ARGS.put("g_tag", tag);
        ARGS.put("g_component", obj);
        ARGS.put("g_action", action);
        ARGS.put("g_json_url", gate.getJsonUrl(tag, "json"));

        if (gate.security != null) {
            for (Entry entry : gate.security.entrySet()) {
                ARGS.put((String) entry.getKey(), entry.getValue());
            }
            if (!is_group(gate,actionHash)) { return new Error("404"); }
        }
        ARGS.put("_gtime", gate.setWhen());
      
        Invoke.invoke0(filterClass, filter, "initialize", new Class[]{Gate.class, String.class, String.class, Map.class}, new Object[]{gate, action, obj, ARGS});
        System.err.println("Filter initialized");
                
        List<String> fk = null;
        if (gate.security != null && !gate.get_role().getIs_admin()) {
            Map<String,List<String>> fks = (Map<String,List<String>>) Invoke.invokeGet(filterClass, filter, "getFks");
            if (fks.containsKey(who)) { fk = fks.get(who); }
        }
        
        List<Map<String,Object>> extras = new ArrayList<>();
        if (fk != null) {
            Error err = assign_fk(gate, fk, ARGS, extras.get(0));
            if (err != null) { return err; }
        }

        Error err = (Error) Invoke.invokeGet(filterClass, filter, "preset");
        if (err !=null) { return err; }
        if (!validate(ARGS, actionHash)) { return new Error("1035"); }
        System.err.println("OK preset");
        
        Class modelClass = Class.forName(gate.config.getProject_name()+"." + obj + ".Model");
        //Object model = modelClass.newInstance();
        Object model = modelClass.getDeclaredConstructor(Object.class).newInstance(c_json);
        Invoke.invokeSet(modelClass, model, "setARGS", Map.class, ARGS);
        System.err.println(gate.config.getProject_name()+"." + obj + ".Model created");
        
        Connection dbh = null;
        if (!no_db(actionHash)) {
            List<String> db = gate.config.getDb();
            try {
                Class.forName((String)getServletContext().getAttribute("jdbctype"));
                dbh = DriverManager.getConnection(db.get(0), db.get(1), db.get(2));
                Invoke.invokeSet(modelClass, model, "setDBH", Connection.class, dbh);
                System.err.println(db.get(0) + " connected");
            } catch (SQLException ex) {
                Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        err = (Error) Invoke.invoke(filterClass, filter, "before", new Class[]{Object.class, List.class}, new Object[]{model, extras});
        if (err != null) { ifnull(dbh); return err; }
        System.err.println("OK before");

        if (!no_method(actionHash)) {
            System.err.println("Model starts " + action);
            err = (Error) Invoke.invoke(modelClass, model, action, new Class[]{List.class}, new Object[]{extras});
            if (err != null) { ifnull(dbh); return err; }
            System.err.println("Model OK " + action);
        }
        
        List<Map<String,Object>> lists = (List<Map<String,Object>>) Invoke.invokeGet(modelClass, model, "getLISTS");
        Map<String,Object> other = (Map<String,Object>) Invoke.invokeGet(modelClass, model, "getOTHER");
        if (fk != null) {
            err = assign_fk_tobe(gate, ARGS, fk, lists);
            if (err!=null) { ifnull(dbh); return err; }
        }       
        
        err = (Error) Invoke.invoke(filterClass, filter, "after", new Class[]{Object.class}, new Object[]{model});
        if (err != null) { ifnull(dbh); return err; }
        ifnull(dbh);
        System.err.println("OK after");
        
        if (gate.get_chartag().getCase()>0) {
            gate.response.setCharacterEncoding("UTF8");
            gate.response.setContentType("application/json");
            PrintWriter writer = gate.response.getWriter();
            Map<String,Object>output = new HashMap<>();
            output.put("LISTS", lists);
            output.put("ARGS", ARGS);
            if (other != null && !other.isEmpty()) { output.put("OTHER", other); }
            String jsonString = new Gson().toJson(output); 
            writer.write(jsonString);
            return null;
        }
 
        String tmpl = (String) Invoke.invokeGet(filterClass, filter, "get_tmpl");
        System.err.println("Run template: " + tmpl);
        gate.request.setAttribute("LISTS", lists);
        gate.request.setAttribute("ARGS", ARGS);
        if (other != null && !other.isEmpty()) { gate.request.setAttribute("OTHER", other); }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(tmpl); 
        dispatcher.forward(gate.request, gate.response);
        
        return null;
    }
    
    private void ifnull(Connection dbh) {
        if (dbh!=null) { try {
            dbh.close();
            } catch (SQLException ex) {
                Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String get_action(Gate gate, Map<String,Map<String,List<String>>> actions) {
        String action_name = gate.config.getAction_name();
        String name = gate.request.getParameter(action_name);
        if (name==null) { 
            String method = gate.request.getMethod();
            if ("GET".equals(method) && null != gate.request.getAttribute("_gid_url")) {
                method = "GET_item";
            }
            name = gate.config.getDefault_actions().get(method);
        }
        if (actions.containsKey(name)) {
            return name;
        }
        for (String par : actions.keySet()) {
            Map<String,List<String>> var = actions.get(par);
            if (var.containsKey("aliases") && Base.grep(par, var.get("aliases"))) {
               return par;
            }
        }
        return "";
    }
    
    public Boolean validate(Map<String, Object> ARGS, Map<String,List<String>> actionHash) {
        if (!actionHash.containsKey("validate")) { return true; }
        for (String field : actionHash.get("validate")) {
            if (!ARGS.containsKey(field)) { return false; }
        }
        return true;
    }
    
    public Boolean is_group(Gate gate, Map<String,List<String>> actionHash) {
        Role role = gate.get_role();
        if (role.getIs_admin()) return true;
        
        if (!actionHash.containsKey("groups")) { return false; }
        for (String field : actionHash.get("groups")) {
            if (gate.role_value.equals(field)) { return true; }
        }
        return false;      
    }
    
    public Boolean no_db(Map<String,List<String>> actionHash) {
        if (!actionHash.containsKey("options")) { return false; }
        for (String field : actionHash.get("options")) {
            if ("no_db".equals(field)) { return true; }
        }
        return false;
    }
    
    public Boolean no_method(Map<String,List<String>> actionHash) {
        if (!actionHash.containsKey("options")) { return false; }
        for (String field : actionHash.get("options")) {
            if ("no_method".equals(field)) { return true; }
        }
        return false;
    }
    
    public Error assign_fk(Gate gate, List<String> fk, Map<String, Object>ARGS, Map<String,Object> extra) {
        String name = fk.get(0);
        if (name==null || "".equals(name)) { return null; }
        String value = (String) ARGS.get(name);
        if (value==null) { return new Error("1041"); }
        extra.put(name, ARGS.get(name));
        
        Role role = gate.get_role();
        String roleid = role.getId_name();
        if (name.equals(roleid)) { return null; }
        
        if ("".equals(gate.config.getSecret())) { return null; }
        if ("".equals(fk.get(1))) { return new Error("1054"); }
        if (ARGS.get(fk.get(1))==null) { return new Error("1055"); }
        String md5 = (String) ARGS.get(fk.get(1));
        String stamp = gate.security.get("X-Forwarded-Time");
        String value_roleid = (String) ARGS.get(roleid);
        try {
            if (!Gate.digest(gate.config.getSecret(), stamp + gate.role_value + roleid + value_roleid + name + value).equals(md5)) {
                return new Error("1052");
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(GeneletFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (role.getDuration()>0) {
            int gtime = (int) ARGS.get("_gtime");
            if (gtime > Integer.parseInt(stamp)) { return new Error("1053"); }
        }
        return null;
    }
    
    private void fk_tobe(Gate gate, List<Map<String,Object>> lists, List<String> fk, String stamp, String roleid, String value_roleid) {
        if (fk.get(2)==null || "".equals(fk.get(2)) || fk.get(2).equals(roleid) || fk.get(3)==null || "".equals(fk.get(3)) ) { return; }
        
        String name = fk.get(2);
        for (Map<String,Object> item : lists) {
            if (!item.containsKey(name)) { continue; }
            String value = (String) item.get(name);
            try {
                item.put(fk.get(3), Base.digest(gate.config.getSecret(), stamp + gate.role_value + roleid + value_roleid + name + value));
            } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
                Logger.getLogger(GeneletFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public Error assign_fk_tobe(Gate gate, Map<String, Object>ARGS, List<String> fk0, List<Map<String,Object>> lists) {
        if ("".equals(gate.config.getSecret())) { return null; }
        Role role = gate.get_role();
        String roleid = role.getId_name();
        String value_roleid = (String) ARGS.get(roleid);
        String stamp = gate.security.get("X-Forwarded-Time");
        
        List<String> fk = new ArrayList<>(fk0);
        fk_tobe(gate, lists, fk, stamp, roleid, value_roleid);
        
        while (fk.size()>4) {
            fk.remove(0);
            fk.remove(0);
            fk.remove(0);
            String which = fk.get(1);
            if (!lists.get(0).containsKey(which)) { return new Error("1056"); }
            for (Map<String, Object> item : lists) {
                List<Map<String,Object>> sublists = (List<Map<String,Object>>) item.get(which);
                fk_tobe(gate, sublists, fk, stamp, roleid, value_roleid);
            }
        }
        
        return null;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GeneletServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Genelet Framework Default Server";
    }// </editor-fold>
}
