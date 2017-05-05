/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author Peter
 */
public class Role  {
    private String Id_name;
    private int Type_id;
    private Boolean Is_admin;
    private List<String> Attributes;

    private String Coding;
    private String Secret;
    private String Surface;
    private int Length;
    private int Duration;
    private List<String> Userlist;
    private List<String> Grouplist;
    private String Logout;
    private String Domain;
    private String Path;
    private int Max_age;
    private Map<String,Issuer> Issuers;

    public Role(JsonObject v) {
        this.Id_name = v.getString("Id_name");
        this.Type_id = v.getInt("Type_id",0);
        this.Is_admin = v.getBoolean("Is_admin",false);
        this.Attributes = Config.getList(v, "Attributes");
        this.Coding = v.getString("Coding");
        this.Secret = v.getString("Secret");
        this.Surface = v.getString("Surface");
        this.Length = v.getInt("Length",0);
        this.Duration = v.getInt("Duration");
        this.Userlist = Config.getList(v, "Userlist");
        this.Grouplist = Config.getList(v, "Grouplist");
        this.Logout = v.getString("Logout","/");
        this.Domain = v.getString("Domain","");
        this.Path = v.getString("Path","/");
        this.Max_age = v.getInt("Max_age");
        JsonObject issuers = v.getJsonObject("Issuers");
        if (!issuers.isEmpty()) {
            this.Issuers = new HashMap<>();
            for (Entry<String, JsonValue> entry: issuers.entrySet()) {
                this.Issuers.put(entry.getKey(), new Issuer((JsonObject) entry.getValue()));
            }
        }
    }
  
    /**
     * @return the Id_name
     */
    public String getId_name() {
        return Id_name;
    }

    /**
     * @param Id_name the Id_name to set
     */
    public void setId_name(String Id_name) {
        this.Id_name = Id_name;
    }

    /**
     * @return the Type_id
     */
    public int getType_id() {
        return Type_id;
    }

    /**
     * @param Type_id the Type_id to set
     */
    public void setType_id(int Type_id) {
        this.Type_id = Type_id;
    }

    /**
     * @return the Is_admin
     */
    public Boolean getIs_admin() {
        return Is_admin;
    }

    /**
     * @param Is_admin the Is_admin to set
     */
    public void setIs_admin(Boolean Is_admin) {
        this.Is_admin = Is_admin;
    }

    /**
     * @return the Coding
     */
    public String getCoding() {
        return Coding;
    }

    /**
     * @param Coding the Coding to set
     */
    public void setCoding(String Coding) {
        this.Coding = Coding;
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
     * @return the Surface
     */
    public String getSurface() {
        return Surface;
    }

    /**
     * @param Surface the Surface to set
     */
    public void setSurface(String Surface) {
        this.Surface = Surface;
    }

    /**
     * @return the Logout
     */
    public String getLogout() {
        return Logout;
    }

    /**
     * @param Logout the Logout to set
     */
    public void setLogout(String Logout) {
        this.Logout = Logout;
    }

    /**
     * @return the Domain
     */
    public String getDomain() {
        return Domain;
    }

    /**
     * @param Domain the Domain to set
     */
    public void setDomain(String Domain) {
        this.Domain = Domain;
    }

    /**
     * @return the Path
     */
    public String getPath() {
        return Path;
    }

    /**
     * @param Path the Path to set
     */
    public void setPath(String Path) {
        this.Path = Path;
    }

    /**
     * @return the Max_age
     */
    public int getMax_age() {
        return Max_age;
    }

    /**
     * @param Max_age the Max_age to set
     */
    public void setMax_age(int Max_age) {
        this.Max_age = Max_age;
    }

    /**
     * @return the Attributes
     */
    public List<String> getAttributes() {
        return Attributes;
    }

    /**
     * @param Attributes the Attributes to set
     */
    public void setAttributes(List<String> Attributes) {
        this.Attributes = Attributes;
    }

    /**
     * @return the Userlist
     */
    public List<String> getUserlist() {
        return Userlist;
    }

    /**
     * @param Userlist the Userlist to set
     */
    public void setUserlist(List<String> Userlist) {
        this.Userlist = Userlist;
    }

    /**
     * @return the Grouplist
     */
    public List<String> getGrouplist() {
        return Grouplist;
    }

    /**
     * @param Grouplist the Grouplist to set
     */
    public void setGrouplist(List<String> Grouplist) {
        this.Grouplist = Grouplist;
    }

    /**
     * @return the Issuers
     */
    public Map getIssuers() {
        return Issuers;
    }

    /**
     * @param Issuers the Issuers to set
     */
    public void setIssuers(Map Issuers) {
        this.Issuers = Issuers;
    }

    /**
     * @return the Length
     */
    public int getLength() {
        return Length;
    }

    /**
     * @param Length the Length to set
     */
    public void setLength(int Length) {
        this.Length = Length;
    }

    /**
     * @return the Duration
     */
    public int getDuration() {
        return Duration;
    }

    /**
     * @param Duration the Duration to set
     */
    public void setDuration(int Duration) {
        this.Duration = Duration;
    }
    
}

