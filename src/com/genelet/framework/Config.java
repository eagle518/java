/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author Peter
 */
public class Config {

    private String Project_name;
    private String Template;
    private String Pubrole;
    private String Secret;
    private String Document_root;
    private String Script_name;
    private String Action_name;
    private HashMap<String,String> Default_actions;
    private String Role_name;
    private String Tag_name;
    private String Provider_name;
    private String Callback_name;
    private String Go_uri_name;
    private String Go_probe_name;
    private String Go_err_name;
    private String Login_name;
    private String Logout_name;
    private String Plain_provider;
    private String Google_provider;
    private List<String> Db;
    private Map Roles;
    private Map Chartags;
    private Blks Blks;
    private List<List<Object>> Static;
    
    public Config(String filename) throws FileNotFoundException, IOException {
        this(get_json(filename));
    }
    
    private static JsonObject get_json(String filename) throws FileNotFoundException, IOException  {
        FileReader f = new FileReader( filename );
        JsonReader reader = Json.createReader(f);
        JsonObject json = reader.readObject();
        f.close();
        return json;
    }
    
    public Config(JsonObject json) {
        this.Go_probe_name = json.getString("Go_probe_name", "go_probe");
        this.Go_err_name = json.getString("Go_err_name", "go_err");
        this.Go_uri_name = json.getString("Go_uri_name", "go_uri");
        this.Secret = json.getString("Secret","");
        this.Action_name = json.getString("Action_name", "action");
        this.Role_name = json.getString("Role_name", "role");
        this.Tag_name = json.getString("Tag_name", "tag");
        this.Provider_name = json.getString("Provider_name", "provider");
        this.Callback_name = json.getString("Callbakc_name", "callback");
        this.Default_actions = new HashMap<String,String>() {{ 
            put("GET", "topics");
            put("GET_item","edit");
            put("PUT","update");
            put("POST", "insert");
            put("DELETE", "delete"); }};
        this.Document_root = json.getString("Document_root");
        this.Script_name = json.getString("Script_name");
        this.Project_name = json.getString("Project_name","");
        this.Pubrole = json.getString("Pubrole","");
        this.Template = json.getString("Template","");
        this.Login_name = json.getString("Login_name","login");
        this.Logout_name = json.getString("Logout_name","logout");
        this.Plain_provider = json.getString("Plain_provider", "plain");
        this.Google_provider = json.getString("Google_provider", "google");
        
        this.Db = getList(json, "Db");
        this.Static = getListList(json, "Static");
        
        JsonObject actions = json.getJsonObject("Default_actions");
        if (actions != null && !actions.isEmpty()) {
            Set<String>keys = actions.keySet();
            keys.stream().forEach((key) -> {
                this.Default_actions.put(key, actions.getString(key));
            });
        }

        JsonObject chartags = json.getJsonObject("Chartags");
        if (chartags!=null && !chartags.isEmpty()) {
            this.Chartags = new HashMap<>();
            for (Entry<String, JsonValue> entry: chartags.entrySet()) {
                this.Chartags.put(entry.getKey(), new Chartag((JsonObject) entry.getValue()));
            }
        }

        JsonObject roles = json.getJsonObject("Roles");
        if (roles!=null && !roles.isEmpty()) {
            this.Roles = new HashMap<>();
            for (Entry<String, JsonValue> entry: roles.entrySet()) {
                this.Roles.put(entry.getKey(), new Role((JsonObject) entry.getValue()));
            }
        }
        JsonObject blks = json.getJsonObject("Blks");
        if (blks!=null && !blks.isEmpty()) {
            this.Blks = new Blks(blks);
        }
    }
    
    public static List<String> getList(JsonObject json, String field) {
        List<String> list = new ArrayList<>();
        JsonArray attrs = json.getJsonArray(field);
        if (attrs != null && !attrs.isEmpty()) {
            for (JsonValue a : attrs) {
                list.add(a.toString().replaceAll("\"", ""));
            }
        }
        return list;
    }
    
    public static List<List<Object>> getListList(JsonObject json, String field) {
        List<List<Object>> list = new ArrayList<>();
        JsonArray attrs = json.getJsonArray(field);
        if (attrs != null && !attrs.isEmpty()) {
            for (JsonValue a : attrs) {
                if (a != null) {
                    List<Object> sublist = new ArrayList<>();
                    int i=0;
                    for (JsonValue b : (JsonArray) a) {
                        String str = b.toString().replaceAll("\"", "");
                        if (i==0) {
                            sublist.add(Pattern.compile(str));
                        } else {
                            sublist.add(str);
                        }
                        i++;
                    }
                    list.add(sublist);
                }
            }
        }
        return list;
    }
    
    public static Map<String,String> getMap(JsonObject json, String field) {
        Map<String,String>hash = new HashMap<>();
        JsonObject object = json.getJsonObject(field);
        if (object != null && !object.isEmpty()) {
            Set<String>keys = object.keySet();
            for (String key : keys) {
                hash.put(key, object.getString(key));
            }
        }
        return hash;
    }
    /**
     * @return the Template
     */
    public String getTemplate() {
        return Template;
    }

    /**
     * @param Template the Template to set
     */
    public void setTemplate(String Template) {
        this.Template = Template;
    }

    /**
     * @return the Pubrole
     */
    public String getPubrole() {
        return Pubrole;
    }

    /**
     * @param Pubrole the Pubrole to set
     */
    public void setPubrole(String Pubrole) {
        this.Pubrole = Pubrole;
    }

    /**
     * @return the Secret
     */
    public String getSecret() {
        return Secret;
    }

    /**
     * @param Secret the Secret to set
     */
    public void setSecret(String Secret) {
        this.Secret = Secret;
    }

    /**
     * @return the Document_root
     */
    public String getDocument_root() {
        return Document_root;
    }

    /**
     * @param Document_root the Document_root to set
     */
    public void setDocument_root(String Document_root) {
        this.Document_root = Document_root;
    }

    /**
     * @return the Script_name
     */
    public String getScript_name() {
        return Script_name;
    }

    /**
     * @param Script_name the Script_name to set
     */
    public void setScript_name(String Script_name) {
        this.Script_name = Script_name;
    }

    /**
     * @return the Action_name
     */
    public String getAction_name() {
        return Action_name;
    }

    /**
     * @param Action_name the Action_name to set
     */
    public void setAction_name(String Action_name) {
        this.Action_name = Action_name;
    }

    /**
     * @return the Default_action
     */
    public HashMap<String,String> getDefault_actions() {
        return Default_actions;
    }

    /**
     * @param Default_actions the Default_actions to set
     */
    public void setDefault_actions(HashMap<String,String> Default_actions) {
        this.Default_actions = Default_actions;
    }

    /**
     * @return the Role_name
     */
    public String getRole_name() {
        return Role_name;
    }

    /**
     * @param Role_name the Role_name to set
     */
    public void setRole_name(String Role_name) {
        this.Role_name = Role_name;
    }

    /**
     * @return the Tag_name
     */
    public String getTag_name() {
        return Tag_name;
    }

    /**
     * @param Tag_name the Tag_name to set
     */
    public void setTag_name(String Tag_name) {
        this.Tag_name = Tag_name;
    }

    /**
     * @return the Provider_name
     */
    public String getProvider_name() {
        return Provider_name;
    }

    /**
     * @param Provider_name the Provider_name to set
     */
    public void setProvider_name(String Provider_name) {
        this.Provider_name = Provider_name;
    }

    /**
     * @return the Callback_name
     */
    public String getCallback_name() {
        return Callback_name;
    }

    /**
     * @param Callback_name the Callback_name to set
     */
    public void setCallback_name(String Callback_name) {
        this.Callback_name = Callback_name;
    }

    /**
     * @return the Go_uri_name
     */
    public String getGo_uri_name() {
        return Go_uri_name;
    }

    /**
     * @param Go_uri_name the Go_uri_name to set
     */
    public void setGo_uri_name(String Go_uri_name) {
        this.Go_uri_name = Go_uri_name;
    }

    /**
     * @return the Go_probe_name
     */
    public String getGo_probe_name() {
        return Go_probe_name;
    }

    /**
     * @param Go_probe_name the Go_probe_name to set
     */
    public void setGo_probe_name(String Go_probe_name) {
        this.Go_probe_name = Go_probe_name;
    }

    /**
     * @return the Go_err_name
     */
    public String getGo_err_name() {
        return Go_err_name;
    }

    /**
     * @param Go_err_name the Go_err_name to set
     */
    public void setGo_err_name(String Go_err_name) {
        this.Go_err_name = Go_err_name;
    }

    /**
     * @return the Db
     */
    public List<String> getDb() {
        return Db;
    }

    /**
     * @param Db the Db to set
     */
    public void setDb(List<String> Db) {
        this.Db = Db;
    }

    /**
     * @return the Roles
     */
    public Map getRoles() {
        return Roles;
    }

    /**
     * @param Roles the Roles to set
     */
    public void setRoles(Map Roles) {
        this.Roles = Roles;
    }

    /**
     * @return the Chartags
     */
    public Map getChartags() {
        return Chartags;
    }

    /**
     * @param Chartags the Chartags to set
     */
    public void setChartags(Map Chartags) {
        this.Chartags = Chartags;
    }

    /**
     * @return the Blks
     */
    public Blks getBlks() {
        return Blks;
    }

    /**
     * @param Blks the Blks to set
     */
    public void setBlks(Blks Blks) {
        this.Blks = Blks;
    }

    /**
     * @return the Static
     */
    public List<List<Object>> getStatic() {
        return Static;
    }

    /**
     * @param Static the Static to set
     */
    public void setStatic(List<List<Object>> Static) {
        this.Static = Static;
    }

    /**
     * @return the Project_name
     */
    public String getProject_name() {
        return Project_name;
    }

    /**
     * @param Project_name the Project_name to set
     */
    public void setProject_name(String Project_name) {
        this.Project_name = Project_name;
    }

    /**
     * @return the Login_name
     */
    public String getLogin_name() {
        return Login_name;
    }

    /**
     * @param Login_name the Login_name to set
     */
    public void setLogin_name(String Login_name) {
        this.Login_name = Login_name;
    }

    /**
     * @return the Logout_name
     */
    public String getLogout_name() {
        return Logout_name;
    }

    /**
     * @param Logout_name the Logout_name to set
     */
    public void setLogout_name(String Logout_name) {
        this.Logout_name = Logout_name;
    }

    /**
     * @return the Plain_provider
     */
    public String getPlain_provider() {
        return Plain_provider;
    }

    /**
     * @param Plain_provider the Plain_provider to set
     */
    public void setPlain_provider(String Plain_provider) {
        this.Plain_provider = Plain_provider;
    }

    /**
     * @return the Google_provider
     */
    public String getGoogle_provider() {
        return Google_provider;
    }

    /**
     * @param Google_provider the Google_provider to set
     */
    public void setGoogle_provider(String Google_provider) {
        this.Google_provider = Google_provider;
    }

}
