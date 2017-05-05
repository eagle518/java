/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.List;
import java.util.Map;
import javax.json.JsonObject;

/**
 *
 * @author Peter
 */
public class Issuer {
    private Boolean Default;
    private int Screen;
    private String Sql;
    private String Sql_as;
    private Map<String,String> Provider_pars;
    private List<String> Credential;
    private List<String> In_pars;
    private List<String> Out_pars;

    public Issuer(JsonObject v) {
        this.Sql = v.getString("Sql","");
        this.Sql_as = v.getString("Sql_as","");
        this.Default= v.getBoolean("Default",false);
        this.Screen = v.getInt("Screen",0);
        this.Credential = Config.getList(v, "Credential");
        this.In_pars = Config.getList(v, "In_pars");
        this.Out_pars = Config.getList(v, "Out_pars");
        this.Provider_pars = Config.getMap(v, "Provider_pars");
    }
    
    /**
     * @return the Default
     */
    public Boolean getDefault() {
        return Default;
    }

    /**
     * @param Default the Default to set
     */
    public void setDefault(Boolean Default) {
        this.Default = Default;
    }

    /**
     * @return the Screen
     */
    public int getScreen() {
        return Screen;
    }

    /**
     * @param Screen the Screen to set
     */
    public void setScreen(int Screen) {
        this.Screen = Screen;
    }

    /**
     * @return the Sql
     */
    public String getSql() {
        return Sql;
    }

    /**
     * @param Sql the Sql to set
     */
    public void setSql(String Sql) {
        this.Sql = Sql;
    }

    /**
     * @return the Sql_as
     */
    public String getSql_as() {
        return Sql_as;
    }

    /**
     * @param Sql_as the Sql_as to set
     */
    public void setSql_as(String Sql_as) {
        this.Sql_as = Sql_as;
    }

    /**
     * @return the Provider_pars
     */
    public Map getProvider_pars() {
        return Provider_pars;
    }

    /**
     * @param Provider_pars the Provider_pars to set
     */
    public void setProvider_pars(Map Provider_pars) {
        this.Provider_pars = Provider_pars;
    }

    /**
     * @return the Credential
     */
    public List<String> getCredential() {
        return Credential;
    }

    /**
     * @param Credential the Credential to set
     */
    public void setCredential(List<String> Credential) {
        this.Credential = Credential;
    }

    /**
     * @return the In_pars
     */
    public List<String> getIn_pars() {
        return In_pars;
    }

    /**
     * @param In_pars the In_pars to set
     */
    public void setIn_pars(List<String> In_pars) {
        this.In_pars = In_pars;
    }

    /**
     * @return the Out_pars
     */
    public List<String> getOut_pars() {
        return Out_pars;
    }

    /**
     * @param Out_pars the Out_pars to set
     */
    public void setOut_pars(List<String> Out_pars) {
        this.Out_pars = Out_pars;
    }



}